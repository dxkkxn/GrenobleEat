import java.util.List;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public class DelegateCompleter implements Completer {

    private Completer delegate;

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        delegate.complete(reader, line, candidates);
    }

    public void setCompleter(Completer delegate) {
        this.delegate = delegate;
    }
}
