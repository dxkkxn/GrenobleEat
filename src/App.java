import java.sql.*;
import java.util.List;
import java.time.*;
import java.time.format.*;

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
                                                      "passer_commande", "disconnect",
                                                      "quit", "deleteAccount");
    private StringsCompleter notConnectedCompleter = new StringsCompleter("connect", "quit");

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
            ("jdbc:mariadb://localhost/grenoble_eat", "dxkkxn", "dxkkxn");

            System.out.println("Connected succesfully to database");
            Client.setConnection(connection);
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
                disconnectDatabase();
                break;
            case "browseCategories":
                quitBrowseMode = false;
                browseCategories();
                break;
            case "passerCommande":
                placeOrder();
                break;
            case "quit":
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
        prompt = "GrenobleEat >\nChoisissez un restaurant > ";
        boolean restaurantChoisi = false;
        String line;
        ParsedLine pl = null;
        while(!restaurantChoisi){
            line = reader.readLine(prompt);
            pl = reader.getParser().parse(line, 0);
            if(pl.words().size() == 1){
                restaurantChoisi = Client.checkRestau(pl.words().get(0));
            }
        }
        String nomRestaurant = pl.words().get(0);
        String mailRestaurant = Client.getMailRestaurant(nomRestaurant);
        LocalDateTime now = LocalDateTime.now();
        String date = getDate(now);
        String heure = getHeure(now);
        prompt = "GrenobleEat >\nChoisissez un / des plats suivi d'une quantité, puis entrez 'ok' > ";
        int prix = 0;
        while (true) {
            line = reader.readLine(prompt);
            pl = reader.getParser().parse(line, 0);
            if(pl.words().size() == 2){
                String nomPlat = pl.words().get(0);
                if(nomPlat.equals("ok")) break;
                int idPlat = Client.checkRestauAPlat(mailRestaurant, nomPlat);
                if(idPlat != 0){
                    int quantite = Integer.parseInt(pl.words().get(1));
                    prix += quantite * Client.getPrix(mailRestaurant, idPlat);
                    Client.ajoutePlat(date, heure, mailRestaurant, idPlat, quantite);
                }
                else{
                    System.out.println("Plat non trouvé");
                }
            }
        }
        Client.ajouteCommande(date, heure, mailRestaurant, prix);
        prompt = "GrenobleEat >";
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
