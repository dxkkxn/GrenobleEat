import org.jline.reader.*;
import org.jline.reader.impl.completer.StringsCompleter;

public class App
{
    private String prompt;
    private DelegateCompleter s = new DelegateCompleter();
    private LineReader reader = LineReaderBuilder.builder().completer(s).build();
    private boolean quit = false;
    private boolean connected = false;
    public void main() {
        connectDatabase();
        System.out.println("Welcome to GrenobleEat");
        printHelpNotConnected();
        prompt = "GrenobleEat (Not connected)> ";

        s.setCompleter(new StringsCompleter("browseCategories", "connect",
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
                if (connected)
                    switchConnected(pl.words().get(0));
                else
                    switchNotConnected(pl.words().get(0));
                }
            }
    }

    void switchNotConnected(String cmd) {
        switch(cmd){
            case "connect":
                userConnect();
                break;
            case "browseCategories":
                //TODO
                //je suppose que cette fonctionnalité est dispo quand on n'est pas connecté ?
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
        // TODO: fermer la connexion avec la base des donnees;
        return;
    }
    void switchConnected(String cmd) {
        switch(cmd){
            case "disconnect":
                userDisconnect();
                disconnectDatabase();
                break;
            case "browseCategories":
                //TODO
                //je suppose que cette fonctionnalité est dispo quand on n'est pas connecté ?
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

    private void printHelpNotConnected(){
        System.out.println("Commandes disponibles : \n\t - connect\n\t - browseCategories");
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
    }

}
