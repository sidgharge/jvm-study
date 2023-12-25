package come.homeproects.jvmstudy.parser.diagnostics;

public record Diagnostic(String message, int startIndex, int endIndex) {

    @Override
    public String toString() {
        return String.format("%s at index %d", message, startIndex);
    }
}