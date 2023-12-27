package come.homeproects.jvmstudy.parser.lexer;

public record Token(String value, TokenType type, int startIndex, int endIndex, int lineNumber) {

    @Override
    public String toString() {
        return value;
    }
}
