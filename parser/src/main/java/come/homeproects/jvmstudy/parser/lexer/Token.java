package come.homeproects.jvmstudy.parser.lexer;

public record Token(String value, TokenType type, int startIndex, int endIndex) {
}
