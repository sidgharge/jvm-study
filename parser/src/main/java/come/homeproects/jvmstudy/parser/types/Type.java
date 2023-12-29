package come.homeproects.jvmstudy.parser.types;

public record Type(String name) {

    public static Type INT = new Type("int");

    public static Type BOOLEAN = new Type("boolean");

    public static Type UNKNOWN = new Type("unknown");

    @Override
    public String toString() {
        return name;
    }
}
