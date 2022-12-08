
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private static Connection conn;
    private static PreparedStatement subcategoriesStmt;
    private static PreparedStatement resInCatStmt;
    private static PreparedStatement categoriesOfResStmt;
    private static PreparedStatement infoResStmt;
    private static PreparedStatement scheduleDayStmt;
    private static PreparedStatement getPwdStmt;
    private static PreparedStatement getAllRestauStmt;
    private static PreparedStatement checkRestauAPlatStmt;
    private static PreparedStatement getIdUser;
    private static PreparedStatement ajoutePlatStmt;
    private static PreparedStatement getPrixStmt;
    private static PreparedStatement getMailRestaurantStmt;
    private static PreparedStatement ajouteCommandeStmt;
    private static PreparedStatement getIdUserStmt;
    private static PreparedStatement opinionStmt;
    private static PreparedStatement deleteCostumerStmt;
    private static PreparedStatement resInCatFilterStmt;
    private static PreparedStatement updatePrixStmt;
    private static PreparedStatement updateIdStmt;
    private static PreparedStatement ajouteLivraisonStmt;
    private static PreparedStatement ajouteSurPlaceStmt;
    private static PreparedStatement getHeureOuv;


    private static int idUser;

    public static void setConnection(Connection connection){
        conn = connection;
    }

    public void prepareAllStatements() {
        try {
            ajouteSurPlaceStmt = conn.prepareStatement("INSERT INTO CommandeSurPlace VALUES (?, ?, ?, ?, ?, ?)");
            getHeureOuv = conn.prepareStatement("SELECT heureOuverture, heureFermeture "+
                                                        "FROM Horaires WHERE jour LIKE ? " +
                                                        "AND mailRestaurant LIKE ? ");
            updatePrixStmt = conn.prepareStatement("UPDATE Commande SET prixCommande = ? " + 
                                                    "WHERE dateCommande LIKE ? and heureCommande LIKE ?");
            getMailRestaurantStmt = conn.prepareStatement("SELECT mailRestaurant FROM Restaurant WHERE nomRestaurant LIKE ?");
            ajouteCommandeStmt = conn.prepareStatement("INSERT INTO Commande Values(?, ?, ?, ?, ?, ?)");
            ajouteLivraisonStmt = conn.prepareStatement("INSERT INTO CommandeLivraison Values(?, ?, ?, ?, ?)");
            ajouteSurPlaceStmt = conn.prepareStatement("INSERT INTO CommandeSurPlace Values(?, ?, ?, ?, ?, ?)");
            ajoutePlatStmt = conn.prepareStatement("INSERT INTO aPourPlats VALUES( ?, ?, ?, ?, ?, ?)");
            checkRestauAPlatStmt = conn.prepareStatement("SELECT numeroPlat FROM Plat" + 
                                                            " WHERE mailRestaurant LIKE ?" +
                                                            " AND nomPlat LIKE ?");
            getPrixStmt = conn.prepareStatement("SELECT prix FROM Plat" +
                                                " WHERE mailRestaurant LIKE ? AND numeroPlat LIKE ?");
            getIdUserStmt = conn.prepareStatement("SELECT idUtilisateur FROM Client WHERE mailClient LIKE ?");
            getPwdStmt = conn.prepareStatement("SELECT motDePasse FROM Client WHERE mailClient LIKE ?");
            subcategoriesStmt = conn.prepareStatement("SELECT nomCategorieFille FROM aPourCategorieMere"
                                                 +" WHERE nomCategorie LIKE ?");
            resInCatStmt = conn.prepareStatement(
                    " with recursive succesor as"
                    +" (select nomCategorieFille as cat from aPourCategorieMere"
                    +" where nomCategorie LIKE ?"
                    +" union all"
                    +" select nomCategorieFille from succesor, aPourCategorieMere"
                    +" where succesor.cat = aPourCategorieMere.nomCategorie)"
                    +" select distinct r.mailRestaurant, r.nomRestaurant, r.textPresentation, avg(ape.note) as noteMoy"
                    +" from Restaurant r, aPourCategorie apc, succesor s, aPourEvaluation ape"
                    +" where r.mailRestaurant = ape.mailRestaurant and"
                    +" r.mailRestaurant = apc.mailRestaurant and "
                    +"      (apc.nomCategorie = s.cat or"
                    +"      apc.nomCategorie LIKE ?)"
                    +" group by mailRestaurant order by noteMoy desc, r.nomRestaurant asc");

            resInCatFilterStmt = conn.prepareStatement(
                    " with recursive succesor as"
                    +" (select nomCategorieFille as cat from aPourCategorieMere"
                    +" where nomCategorie LIKE ?"
                    +" union all"
                    +" select nomCategorieFille from succesor, aPourCategorieMere"
                    +" where succesor.cat = aPourCategorieMere.nomCategorie)"
                    +" select distinct r.mailRestaurant, r.nomRestaurant, r.textPresentation, avg(ape.note) as noteMoy"
                    +" from Restaurant r, aPourCategorie apc, succesor s, aPourEvaluation ape, Horaires h"
                    +" where r.mailRestaurant = ape.mailRestaurant and r.mailRestaurant = h.mailRestaurant"
                    +" and r.mailRestaurant = apc.mailRestaurant and "
                    +"      (apc.nomCategorie = s.cat or"
                    +"      apc.nomCategorie LIKE ?)"
                    +" and h.jour LIKE ? and h.heureOuverture > ?"
                    +" group by mailRestaurant order by noteMoy desc, r.nomRestaurant asc");
            categoriesOfResStmt = conn.prepareStatement(
            " with recursive ancestor as ("
            +" select nomCategorie as cat from aPourCategorieMere"
            +" where nomCategorieFille IN (SELECT nomCategorie from aPourCategorie where mailRestaurant like ?)"
            +" union all"
            +" select nomCategorie from ancestor, aPourCategorieMere"
            +" where ancestor.cat = aPourCategorieMere.nomCategorieFille)"
            +" select cat from ancestor"
            +" UNION"
            +" SELECT nomCategorie as cat from aPourCategorie where mailRestaurant like ?"
            );
            infoResStmt = conn.prepareStatement(
            " SELECT *, avg(note) as noteMoy"
            +" from Restaurant NATURAL JOIN aPourEvaluation "
            +" where mailRestaurant LIKE ?");
            scheduleDayStmt = conn.prepareStatement(
            "select heureOuverture, heureFermeture"
            +" from Horaires where jour LIKE ? and mailRestaurant LIKE ?"
            +" order by heureOuverture asc");
            getAllRestauStmt = conn.prepareStatement("SELECT nomRestaurant FROM Restaurant");
            opinionStmt = conn.prepareStatement(
                "select avis, note from aPourEvaluation"
                +" where mailRestaurant LIKE ?"
            );
            deleteCostumerStmt = conn.prepareStatement(
                "delete from Client"
                +" where idUtilisateur like ?"
                );
            updateIdStmt = conn.prepareStatement(
            "update Utilisateur"
            +" set idUtilisateur = ?"
            +" where idUtilisateur= ?");


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public Client(Connection conn) {
        this.conn = conn;
        prepareAllStatements();
    }

    public static void setIdUser(String mail){
        try{
            getIdUserStmt.setString(1, mail);
            ResultSet res = getIdUserStmt.executeQuery();
            if(res.next()){
                idUser = res.getInt(1);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void ajouteCommande(String date, String heure, String mailRestaurant, float prix){
        try{
            ajouteCommandeStmt.setString(1, date);
            ajouteCommandeStmt.setString(2, heure);
            ajouteCommandeStmt.setInt(3, idUser);
            ajouteCommandeStmt.setString(4, mailRestaurant);
            ajouteCommandeStmt.setFloat(5, prix);
            ajouteCommandeStmt.setString(6, "attente de confirmation");
            ajouteCommandeStmt.executeQuery();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static int ajouteLivraison(String date, String heure, String mailRestaurant, String address){
        try {
            ajouteLivraisonStmt.setString(1, date);
            ajouteLivraisonStmt.setString(2, heure);
            ajouteLivraisonStmt.setInt(3, idUser);
            ajouteLivraisonStmt.setString(4, mailRestaurant);
            ajouteLivraisonStmt.setString(5, address);
            ajouteLivraisonStmt.executeQuery();
            return 0;
        }
        catch (SQLException e){
            e.printStackTrace();
            return -1;
        }
    }

    public static void setPrixCommande(String date, String heure, float prix){
        try {
            updatePrixStmt.setFloat(1, prix);
            updatePrixStmt.setString(2, date);
            updatePrixStmt.setString(3, heure);
            updatePrixStmt.executeQuery();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getMailRestaurant(String nomRestaurant){
        try{
            getMailRestaurantStmt.setString(1, nomRestaurant);
            ResultSet res = getMailRestaurantStmt.executeQuery();
            if(res.next()) return res.getString(1);
            else return null;
        }
        catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int checkRestauAPlat(String mailRestaurant, String nomPlat){
        try{
            checkRestauAPlatStmt.setString(1, mailRestaurant);
            checkRestauAPlatStmt.setString(2, nomPlat);
            ResultSet res = checkRestauAPlatStmt.executeQuery();
            if(res.next()){
                return res.getInt(1);
            }
            return 0;
        }
        catch(SQLException e){
            e.printStackTrace();
            return 0;
        }
    }

    public static void ajoutePlat(String date, String heure, String mailRestaurant, int idPlat, int quantite){
        try{
            ajoutePlatStmt.setString(1, date);
            ajoutePlatStmt.setString(2, heure);
            ajoutePlatStmt.setInt(3, idUser);
            ajoutePlatStmt.setString(4, mailRestaurant);
            ajoutePlatStmt.setInt(5, idPlat);
            ajoutePlatStmt.setInt(6, quantite);
            ajoutePlatStmt.executeQuery();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static float getPrix(String mailRestaurant, int idPlat){
        try{
            getPrixStmt.setString(1, mailRestaurant);
            getPrixStmt.setInt(2, idPlat);
            ResultSet res = getPrixStmt.executeQuery();
            if(res.next()) return res.getFloat(1);
            else return -1;
        }
        catch (SQLException e){
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean checkRestau(String nom){
        try{
            ResultSet res = getAllRestauStmt.executeQuery();
            while(res.next()){
                if(res.getString(1).equals(nom))return true;
            }
            System.out.println("Ce restaurant n'existe pas !");
            return false;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean essaiConnection(String mailClient, String motDePasse) {
        try{
            getPwdStmt.setString(1, mailClient);
            ResultSet res = getPwdStmt.executeQuery();
            if(res.next() && motDePasse.equals(res.getString(1))){
                return true;
            }
            System.out.println("Mauvais mail ou mot de passe");
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void browseCategories() {
        String root = getRoot();
        System.out.println(root);
        System.out.println(getSubcategories(root));
        System.out.println(getSubcategories(getSubcategories(root).get(0)));
    }

    public void deleteAccount() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("WITH RECURSIVE T AS ("
            +"  SELECT 1 AS n, FLOOR(RAND() * 10) AS v"
            +"  UNION ALL"
            +"  SELECT n + 1, FLOOR(RAND() * 10)"
            +"  FROM T"
            +"  WHERE EXISTS(SELECT * FROM Utilisateur WHERE idUtilisateur = v)"
            +")"
            +" select v from T where n >= ALL(select n from t)");
            res.next();
            int newId = res.getInt(1);
            deleteCostumerStmt.setInt(1, idUser);
            deleteCostumerStmt.executeUpdate();
            updateIdStmt.setInt(1, newId);
            updateIdStmt.setInt(2, idUser);
            updateIdStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace ();
        }
    }
    public String getRoot() {
        String root = "";
        try {
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("SELECT nomCategorie FROM Categories"
                              +" WHERE nomCategorie NOT IN "
                              +" (SELECT nomCategorieFille FROM aPourCategorieMere)");
            int count = 0;
            while (res.next()) {
                root = res.getString(1);
                count++;
            }
            assert (count == 1);
        } catch (SQLException e) {
            e.printStackTrace ();
        }
        return root;
    }
    private void printCategories(String restaurant) {
        try {
            categoriesOfResStmt.setString(1, restaurant);
            categoriesOfResStmt.setString(2, restaurant);
            ResultSet res = categoriesOfResStmt.executeQuery();
            while (!res.last() && res.next()) {
                System.out.print(res.getString(1)+", ");
            }
            System.out.println(res.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void printSchedule(String restaurant) {
        System.out.println("Schedule\n");
        String[] days = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};
        try {
            ResultSet res = null;
            for (String day : days) {
                System.out.println(day);
                scheduleDayStmt.setString(1, day);
                scheduleDayStmt.setString(2, restaurant);
                res = scheduleDayStmt.executeQuery();
                while (res.next()) {
                    System.out.println("Opening time: "+ res.getString("heureOuverture"));
                    System.out.println("Closing time: "+ res.getString("heureFermeture"));
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace ();
        }
    }
    private void printOpinions(String restaurant) {
        System.out.println("Opinions");
        try {
            opinionStmt.setString(1, restaurant);
            ResultSet res = opinionStmt.executeQuery();
            while (res.next()) {
                for (int i = 0; i<res.getInt("note"); i++){
                    System.out.print("✭ ");
                }
                System.out.println(res.getString("avis"));
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace ();
        }

    }
    /*
     * Imprime tous les restaurants appartenant a la categorie category et
     * tous ses filles.
     * @param category
     */
    public void printRestaurant(String restaurant) {
        try {
            infoResStmt.setString(1, restaurant);
            ResultSet res = infoResStmt.executeQuery();
            while (res.next()) {
                System.out.println("Email: " + res.getString("mailRestaurant"));
                System.out.println("Name: " + res.getString("nomRestaurant"));
                System.out.println("Description: " + res.getString("textPresentation"));
                System.out.println("Restaurant address: " + res.getString("adresseRestaurant"));
                System.out.println("Capacity: " + res.getString("nombrePlaces"));
                System.out.println("Average mark: " + res.getString("noteMoy"));
            }
            System.out.print("Categories: ");
            printCategories(restaurant);
            printSchedule(restaurant);
            printOpinions(restaurant);
        } catch (SQLException e) {
            e.printStackTrace ();
        }
    }

    public void printRestaurantsFilter(String category, String day, String hour) {
        try {
            resInCatFilterStmt.setString(1, category);
            resInCatFilterStmt.setString(2, category);
            resInCatFilterStmt.setString(3, day);
            resInCatFilterStmt.setString(4, hour);
            ResultSet res = resInCatFilterStmt.executeQuery();
            while (res.next()) {
                System.out.println("Email: " + res.getString("mailRestaurant"));
                System.out.println("Name: " + res.getString("nomRestaurant"));
                System.out.println("Average mark: " + res.getString("noteMoy"));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace ();
        }
    }
    public void printRestaurants(String category) {
        try {
            resInCatStmt.setString(1, category);
            resInCatStmt.setString(2, category);
            ResultSet res = resInCatStmt.executeQuery();
            while (res.next()) {
                System.out.println("Email: " + res.getString("mailRestaurant"));
                System.out.println("Name: " + res.getString("nomRestaurant"));
                System.out.println("Average mark: " + res.getString("noteMoy"));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace ();
        }
    }
    public List<String> getSubcategories(String category) {
        List<String> subcategories = new ArrayList<>();
        try {
            subcategoriesStmt.setString(1, category);
            ResultSet res = subcategoriesStmt.executeQuery();
            while (res.next()) {
                subcategories.add(res.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace ();
        }
        return subcategories;

    }

    //TODO
    public static boolean checkHeureEtCapacite(String mailRestaurant, String heure, int day){
        String jour = null;
        switch(day){
            case 2:
                jour = "Lundi";
                break;
            case 3:
                jour = "Mardi";
                break;
            case 4:
                jour = "Mercredi";
                break;
            case 5:
                jour = "Jeudi";
                break;
            case 6:
                jour = "Vendredi";
            default:
                return false;
        }

        ResultSet res;
        try{
            getHeureOuv.setString(1, jour);
            getHeureOuv.setString(2, mailRestaurant);
            res = getHeureOuv.executeQuery();
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        try{
            res.next();
            String heureOuverture = res.getString(1);
            String heureFermeture = res.getString(2);
            if(heure.compareTo(heureOuverture) >= 0 && heure.compareTo(heureFermeture) <= 0){
                return true;
            }
            res.next();
            heureOuverture = res.getString(1);
            heureFermeture = res.getString(2);
            if(heure.compareTo(heureOuverture) >= 0 && heure.compareTo(heureFermeture) <= 0){
                return true;
            }
            return false;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static int ajouteSurPlace(String date, String heure, String mailRestaurant, 
                                        int nbPersonnes, String heureArrivee){
        try{
            ajouteSurPlaceStmt.setString(1, date);
            ajouteSurPlaceStmt.setString(2, heure);
            ajouteSurPlaceStmt.setInt(3,idUser);
            ajouteSurPlaceStmt.setString(4, mailRestaurant);
            ajouteSurPlaceStmt.setInt(5, nbPersonnes);
            ajouteSurPlaceStmt.setString(6, heureArrivee);
            ajouteSurPlaceStmt.executeQuery();
            return 0;
        }
        catch (SQLException e){
            e.printStackTrace();
            return -1;
        }
    }
}


