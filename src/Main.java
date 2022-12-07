import org.jline.reader.*;
import org.jline.reader.impl.completer.StringsCompleter;


public class Main
{
    private static String prompt = "GrenobleEat>";
    private static StringsCompleter s = new StringsCompleter("parcourir_categories", "abcdef", "connect", "passer_commande", "disconnect");
    private static LineReader reader = LineReaderBuilder.builder().completer(s).build();

    public static void main(String[] args) {
        while (true) {
            String line = null;
            try {
                line = reader.readLine(prompt);
                ParsedLine pl = reader.getParser().parse(line, 0);
                //pl.words().subList(1, pl.words().size()).toArray(new String[0]);
                //System.out.println(pl.words());

                //1 mot par line, pour des commandes simples
                if(pl.words().size() == 1){
                    switch(pl.words().get(0)){
                        case "connect":
                            userConnect();
                            break;
                        case "parcourir_categories":
                            //TODO
                            //je suppose que cette fonctionnalité est dispo quand on n'est pas connecté ?
                            break;

                        default:
                            printHelpNotConnected();
                            break;
                    }
                }
            } catch (UserInterruptException e) {
                // Ignore
            } catch (EndOfFileException e) {
                return;
            }
        }

    }
    static private void printHelpNotConnected(){
        System.out.println("Commandes disponibles : \n\t - connect\n\t - parcourir_categories");
    }

    static private void printHelpConnected(){
        System.out.println("Commandes disponibles : \n\t - disconnect\n\t - parcourir_categories\n\t - passer_commande");
    }

    static private void userConnect(){
        String promptUser = prompt + " mail :";
        String promptPwd = prompt + " password :";
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
            connectionOk = Client.essaiConnection(pl.words().get(0), pl2.words().get(0));
            //System.out.println("mail : " + pl.words().get(0) + "\npassword : " + pl2.words().get(0));
        }
        System.out.println("connectionOk");

        //ici, l'user est connecté
        while(true){
            //TODO : get username et l'afficher
            String promptConnected = "Welcome To "+ prompt; 
            line = reader.readLine(promptConnected);
            ParsedLine pl = reader.getParser().parse(line, 0);
            //1 mot par line, pour des commandes simples
            if(pl.words().size() == 1){
                switch(pl.words().get(0)){
                    case "disconnect":
                        System.out.println("Goodbye !"), //+username ?
                        return ;
                    case "passer_commande":
                        passCmd();
                        break;
                    default:
                        printHelpConnected();
                        break;
                }
            }
        }
    }

    static private void passCmd(){
        while(true){
            //TODO : get username et l'afficher
            line = reader.readLine("Choisissez un restau : ");
            ParsedLine pl = reader.getParser().parse(line, 0);
            //1 mot par line, pour des commandes simples
            if(pl.words().size() == 1){
                switch(pl.words().get(0)){
                    case "help":
                        System.out.println("Goodbye !"), //+username ?
                        return ;
                    case "passer_commande":
                        passCmd();
                        break;
                    default:
                        printHelpConnected();
                        break;
                }
            }
        }
    }
}
