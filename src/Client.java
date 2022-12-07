
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private Connection conn;
    private static PreparedStatement subcategoriesStmt;
    private static PreparedStatement resInCatStmt;
    private static PreparedStatement resInCatFilterStmt;
    private static PreparedStatement categoriesOfResStmt;
    private static PreparedStatement infoResStmt;
    private static PreparedStatement scheduleDayStmt;
    private static PreparedStatement opinionStmt;

    public void prepareAllStatements() {
        try {
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
            opinionStmt = conn.prepareStatement(
                "select avis, note from aPourEvaluation"
                +" where mailRestaurant LIKE ?"
            );


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public Client(Connection conn) {
        this.conn = conn;
        prepareAllStatements();
    }

    public void browseCategories() {
        String root = getRoot();
        System.out.println(root);
        System.out.println(getSubcategories(root));
        System.out.println(getSubcategories(getSubcategories(root).get(0)));
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

}
