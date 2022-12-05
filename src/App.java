import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

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

    public void main() {
        connectDatabase();
        System.out.println("Welcome to GrenobleEat");
        printHelpNotConnected();
        prompt = "GrenobleEat (Not connected)> ";

        s.setCompleter(new StringsCompleter("connect",
                                            "quit"));
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
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void switchBrowsingCategories() {

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
            case "quit":
                disconnectDatabase();
                quit = true;
                break;
            default:
                printHelpConnected();
                break;
        }
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
        while (!quitBrowseMode) {
            try {
                String line = reader.readLine(prompt);
                ParsedLine pl = reader.getParser().parse(line, 0);
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
            } else {
                switch(cmd){
                    case "printRestaurants":
                        printRestaurants(c.getRestaurants(currentCat));
                        break;
                    case "quitBrowseMode":
                        System.out.println("Quiting browsing categories mode");
                        s.setCompleter(new StringsCompleter("browseCategories", "connect",
                                                            "placeOrder", "disconnect",
                                                            "quit"));
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
        s.setCompleter(new StringsCompleter("browseCategories", "connect",
                                            "quit"));
    }
    private void userConnect(){
        String promptUser = "Please enter you email : ";
        String promptPwd = "Please enter you password : ";
        String line = null;
        boolean connectionOk = false;
        while (!connectionOk){
            ParsedLine pl = null;
            while(pl == null || pl.words().size() != 1){
                line = reader.readLine(promptUser);
                pl = reader.getParser().parse(line, 0);
            }
            ParsedLine pl2 = null;
            while(pl2 == null || pl2.words().size() != 1){
                line = reader.readLine(promptPwd);
                pl2 = reader.getParser().parse(line, 0);
            }
            //co = essaiConnection();
            connectionOk = true; 
            System.out.println("mail : " + pl.words().get(0) + "\npassword : " + pl2.words().get(0));
        }
        System.out.println("connection OK");
        connected = true;
        // updatePrompt
        prompt = "GrenobleEat> " ;
        s.setCompleter(new StringsCompleter("browseCategories", "connect",
                                            "placeOrder", "disconnect",
                                            "quit"));
        // creer une instance de client avec les donnees recuperees.
        c = new Client(connection);
    }

}
