import org.jline.reader.*;
import org.jline.reader.impl.completer.StringsCompleter;


public class Main
{
    public static void main(String[] args) {
        StringsCompleter s = new StringsCompleter("parcourir_categories", "abcdef");
        LineReader reader = LineReaderBuilder.builder().completer(s).build();
        String prompt = "GrenobleEat>";
        while (true) {
            String line = null;
            try {
                line = reader.readLine(prompt);
                System.out.println(line);
                ParsedLine pl = reader.getParser().parse(line, 0);
                //pl.words().subList(1, pl.words().size()).toArray(new String[0]);
                System.out.println(pl.words());
            } catch (UserInterruptException e) {
                // Ignore
            } catch (EndOfFileException e) {
                return;
            }
        }

    }
}
