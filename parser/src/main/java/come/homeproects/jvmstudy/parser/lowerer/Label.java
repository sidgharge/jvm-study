package come.homeproects.jvmstudy.parser.lowerer;

public record Label(String name) {

    @Override
    public String toString() {
        return String.format("Label: %s", name);
    }
}
