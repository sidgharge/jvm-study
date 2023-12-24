package come.homeproects.jvmstudy.parser;

import java.util.ArrayList;
import java.util.List;

public class Diagnostics {

    private final List<String> errors;

    public Diagnostics() {
        errors = new ArrayList<>();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> errors() {
        return errors;
    }

    public void addDiagnostic(String error, Object... args) {
        errors.add(String.format(error, args));
    }
}
