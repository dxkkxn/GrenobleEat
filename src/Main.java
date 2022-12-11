public class Main
{
    public static void main(String[] args) {
        System.out.println(args[0]+ ' '+ args[1]);
        if (args.length != 2) {
            System.out.println("Lisez le readme.md");
            return;
        }
        App app = new App(args[0], args[1]);
        app.main();
    }
}
