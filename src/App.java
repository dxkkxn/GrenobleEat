import java.sql.*;
import java.util.List;
import java.time.*;
import java.time.format.*;
import java.util.Calendar;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.StringsCompleter;



public class App
{
    private String prompt;
    private DelegateCompleter s = new DelegateCompleter();
    private LineReader reader = LineReaderBuilder.builder().completer(s).build();
    private boolean quit = false;
    private boolean quitBrowseMode = true;
    private boolean connected = false;
    private boolean browsingCategories = false;
    private Connection connection;
    private Client c;
    private StringsCompleter connectedCompleter = new StringsCompleter("browseCategories", "connect",
                                                      "passerCommande", "disconnect",
                                                      "quit", "deleteAccount");
    private StringsCompleter notConnectedCompleter = new StringsCompleter("connect", "quit");

    enum enumCommande{
        LIVRAISON,
        A_EMPORTER,
        SUR_PLACE;
    }

    public void main() {
        connectDatabase();
        System.out.println("Welcome to GrenobleEat");
        printHelpNotConnected();
        prompt = "GrenobleEat (Not connected)> ";

        s.setCompleter(notConnectedCompleter);
        ParsedLine pl = null;
        String line = null;
        while (!quit) {
            try {
                line = reader.readLine(prompt);
                pl = reader.getParser().parse(line, 0);
            } catch (UserInterruptException e) {
                // Ignore
            } catch (EndOfFileException e) {
                return;
            }
            if (pl.words().size() != 1) {
                printHelpNotConnected();
            } else {
                if (connected) {
                    switchConnected(pl.words().get(0));
                } else {
                    switchNotConnected(pl.words().get(0));
                }
                }
            }
    }

    void connectDatabase() {
        try {
            // Load the JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");
            System.out.println("Driver loaded");
            // Try to connect
            connection = DriverManager.getConnection
            ("jdbc:mariadb://localhost/GrenobleEat", "dxkkxn", "dxkkxn");

            System.out.println("Connected succesfully to database");
            Client.setConnection(connection);
            PreparedStatement autocommitoff = connection.prepareStatement("SET autocommit = 0");
            autocommitoff.executeQuery();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void switchNotConnected(String cmd) {
        switch(cmd){
            case "connect":
                userConnect();
                break;
            case "quit":
                disconnectDatabase();
                quit = true;
                break;
            default:
                printHelpNotConnected();
                break;
        }
    }
    void disconnectDatabase() {
        try {
            connection.close();
            System.out.println("Disconnected succesfully from database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }
    void switchConnected(String cmd) {
        switch(cmd){
            case "disconnect":
                userDisconnect();
                //disconnectDatabase(); on ne déconnecte que l'user pas
                break;
            case "browseCategories":
                quitBrowseMode = false;
                browseCategories();
                break;
            case "passerCommande":
                placeOrder();
                break;
            case "quit":
                userDisconnect();
                disconnectDatabase();
                quit = true;
                break;
            case "deleteAccount":
                c.deleteAccount();
                connected = false;
                s.setCompleter(notConnectedCompleter);
                prompt = "GrenobleEat (Not connected)> " ;

            default:
                printHelpConnected();
                break;
        }
    }

    private void placeOrder(){
        try{
            PreparedStatement save = null;
            save = connection.prepareStatement("SAVEPOINT avantCommande");
            save.executeQuery();
        }
        catch(SQLException e){
            e.printStackTrace();
            return;
        }
        String line;
        ParsedLine pl = null;
        boolean restaurantChoisi = false;
        
        prompt = "Choisissez un restaurant> ";
        while(!restaurantChoisi){
            line = reader.readLine(prompt);
            pl = reader.getParser().parse(line, 0);
            if(pl.words().size() == 1){
                restaurantChoisi = Client.checkRestau(pl.words().get(0));
            }
        }

        String nomRestaurant = pl.words().get(0);
        String mailRestaurant = Client.getMailRestaurant(nomRestaurant);
        
        enumCommande typeCommande = enumCommande.A_EMPORTER;
        boolean surPlace = false;
        prompt = "sur place / a emporter / en livraison>";
        pl = null;
        while(pl == null || pl.words().size() != 2 || !((pl.words().get(0).equals("a") && pl.words().get(1).equals("emporter")) || (pl.words().get(0).equals("sur") && pl.words().get(1).equals("place")) || (pl.words().get(0).equals("en") && pl.words().get(1).equals("livraison")))){
            line = reader.readLine(prompt);
            pl = reader.getParser().parse(line, 0);
        }
        switch(pl.words().get(0)){
            case "sur":
                typeCommande = enumCommande.SUR_PLACE;
                break;
            case "a":
                typeCommande = enumCommande.A_EMPORTER;
                break;
            case "en":
                typeCommande = enumCommande.LIVRAISON;
                break;
            default:
                break;
        }
        
        
        LocalDateTime now = LocalDateTime.now();
        String date = getDate(now);
        String heure = getHeure(now);
        prompt = "Choisissez un / des plats suivi d'une quantité, puis entrez 'valider' ou 'annuler'\n> ";
        float prix = 0;
        Client.ajouteCommande(date, heure, mailRestaurant, 0);
        boolean termine = false;
        while (!termine) {
            line = reader.readLine(prompt);
            pl = reader.getParser().parse(line, 0);
            if(pl.words().size() == 2){
                String nomPlat = pl.words().get(0);
                int idPlat = Client.checkRestauAPlat(mailRestaurant, nomPlat);
                if(idPlat != 0){
                    int quantite = Integer.parseInt(pl.words().get(1));
                    prix += quantite * Client.getPrix(mailRestaurant, idPlat);
                    Client.ajoutePlat(date, heure, mailRestaurant, idPlat, quantite);
                    System.out.println("Panier mis à jour !");
                }
                else{
                    System.out.println("Plat non trouvé");
                }
            }
            else if(pl.words().size() == 1){
                if(pl.words().get(0).equals("valider")){
                    System.out.println("Merci !");
                    termine = true;
                }
                else if(pl.words().get(0).equals("annuler")){
                    rollback();
                    prompt = "GrenobleEat>";
                    return;
                }
            }
        }
        if(prix > 0){
            Client.setPrixCommande(date, heure, prix);
            switch(typeCommande){
                case SUR_PLACE:
                    pl = null;
                    prompt = "Nombre de personnes>";
                    while(pl == null || pl.words().size()!= 1){
                        line = reader.readLine(prompt);
                        pl = reader.getParser().parse(line, 0);
                    }
                    int nbPersonnes = Integer.parseInt(pl.words().get(0));

                    if(nbPersonnes <= 0){
                        System.out.println("Nombre de personnes non valide");
                        rollback();
                        return;
                    }

                    pl = null;
                    prompt = "heure d'arrivee (hh:mm)>";
                    while(pl == null || pl.words().size()!= 1){
                        line = reader.readLine(prompt);
                        pl = reader.getParser().parse(line, 0);
                    }
                    String heureArrivee = pl.words().get(0);
                    heureArrivee += ":00";

                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_WEEK);

                    if(Client.checkHeureEtCapacite(mailRestaurant, heureArrivee, day)){
                        int retVal =  Client.ajouteSurPlace(date, heure,
                                                     mailRestaurant, nbPersonnes, heureArrivee);
                        prompt = "GrenobleEat>";
                        if(retVal == 0){
                            commit();
                            System.out.println("Commande passée !");
                        }
                        else{
                            rollback();
                            return;  
                        } 
                    }
                    else{
                        System.out.println("Désolé, il n'y a plus de place !");
                        rollback();
                    }
                    break;
                case LIVRAISON:
                    pl = null;
                    prompt = "Adresse de livraison>";
                    while(pl == null || pl.words().size() == 0){
                        line = reader.readLine(prompt);
                        pl = reader.getParser().parse(line, 0);
                    }
                    String adresse = concatWords(pl.words());
                    if(adresse != null){
                        int ret = Client.ajouteLivraison(date, heure, mailRestaurant, adresse);
                        if(ret == 0){
                            commit();
                            System.out.println("Commande passée !");
                        }
                        else rollback();
                    }
                    else rollback();
                    break;
                case A_EMPORTER:
                    commit();
                    System.out.println("Commande passée !");
                    break;
                default:
                //n'arrive jamais
                    break; 
            }
        }
        else{
            System.out.println("Commande vide !");
            rollback();
        }
        prompt = "GrenobleEat>";
    }

    private void rollback(){
        try{
            PreparedStatement rollback = connection.prepareStatement("ROLLBACK TO avantCommande");
            rollback.executeQuery();
            return;
        }
        catch (SQLException e){
            e.printStackTrace();
            return;
        }
    }

    private void commit(){
        try{
            PreparedStatement commit = null;
            commit = connection.prepareStatement("COMMIT");
            commit.executeQuery();
        }
        catch(SQLException e){
            e.printStackTrace();
            return;
        }
    }

    private String getDate(LocalDateTime now){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dtf.format(now);
    }

    private String getHeure(LocalDateTime now){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        return dtf.format(now);
    }

    private void printInfoBrowseCat(String currentCat) {
        System.out.println("Current category: " + currentCat);
        System.out.println("Available subcategories : ");
        for (String cat: c.getSubcategories(currentCat)) {
            System.out.println("\t"+cat);
        }
        List<String> completion = c.getSubcategories(currentCat);
        completion.add("printRestaurants");
        completion.add("quitBrowseMode");
        completion.add("quit");
        s.setCompleter(new StringsCompleter(completion));


    }
    private String concatWords(List<String> ls) {
        String res = "";
        for (int i = 0; i < ls.size()-1; i++) {
            res += ls.get(i);
            res += " ";
        }
        res += ls.get(ls.size()-1);
        return res;
    }
    private void browseCategories() {
        String currentCat = c.getRoot();
        printInfoBrowseCat(currentCat);
        String cmd = " ";
        ParsedLine pl = null;
        while (!quitBrowseMode) {
            try {
                String line = reader.readLine(prompt);
                pl = reader.getParser().parse(line, 0);
                cmd = concatWords(pl.words());

            } catch (UserInterruptException e) {
                // Ignore
            } catch (EndOfFileException e) {
                return;
            }
            if (c.getSubcategories(currentCat).contains(cmd)) {
                currentCat = cmd;
                System.out.println();
                printInfoBrowseCat(currentCat);
            } else if (pl.words().get(0).equals("print")) {
                c.printRestaurant(pl.words().get(1));
            } else {
                switch(pl.words().get(0)){
                    case "printRestaurants":
                        if (pl.words().size() == 3)
                            c.printRestaurantsFilter(currentCat,
                                                     pl.words().get(1),
                                                     pl.words().get(2));
                        else
                            c.printRestaurants(currentCat);
                        break;
                    case "quitBrowseMode":
                        System.out.println("Quiting browsing categories mode");
                        s.setCompleter(connectedCompleter);
                        quitBrowseMode = true;
                        break;
                    case "quit":
                        disconnectDatabase();
                        quitBrowseMode = true;
                        quit = true;
                        break;
                    default:
                        printHelpBrowseCat();
                        break;
                }
            }
        }

    }

    private void printRestaurants(List<String> l) {
        for (String s : l) {
            System.out.println(s);
        }
    }


    private void printHelpBrowseCat(){
        System.out.println("Commandes disponibles : \n"
                           +"\t - $(SubCategory) : to change of category\n"
                           +"\t - printRestaurants : to print restaurants of current category"
                           +"\t - quitBrowseMode : to quit browsing categories mode"
                           +"\t - quit");
    }
    private void printHelpNotConnected(){
        System.out.println("Commandes disponibles : \n\t - connect\n\t - quit");
    }
    private void printHelpConnected(){
        System.out.println("Commandes disponibles : \n\t - disconnect\n\t - browseCategories\n\t - passerCommande");
    }
    private void userDisconnect() {
        connected = false;
        prompt = "GrenobleEat (Not connected)> " ;
        s.setCompleter(notConnectedCompleter);
    }
    private void userConnect(){
        c = new Client(connection);
        String promptUser = "Please enter you email : ";
        String promptPwd = "Please enter you password : ";
        String line = null;
        boolean connectionOk = false;
        ParsedLine pl = null;
        while (!connectionOk){
            pl = null;
            while(pl == null || pl.words().size() != 1 || pl.words().get(0).equals("")){
                line = reader.readLine(promptUser);
                pl = reader.getParser().parse(line, 0);
            }
            ParsedLine pl2 = null;
            while(pl2 == null || pl2.words().size() != 1 || pl2.words().get(0).equals("")){
                line = reader.readLine(promptPwd);
                pl2 = reader.getParser().parse(line, 0);
            }
            connectionOk = Client.essaiConnection(pl.words().get(0), pl2.words().get(0));
        }
        Client.setIdUser(pl.words().get(0));
        System.out.println("connection OK");
        connected = true; //redondant avec connectioOk ?
        // updatePrompt
        prompt = "GrenobleEat> " ;
        s.setCompleter(connectedCompleter);
        // creer une instance de client avec les donnees recuperees.
    }
}
