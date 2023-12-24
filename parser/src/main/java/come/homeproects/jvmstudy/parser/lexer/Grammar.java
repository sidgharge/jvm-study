package come.homeproects.jvmstudy.parser.lexer;

import static come.homeproects.jvmstudy.parser.lexer.TokenType.KEYWORD_TRUE;

public class Grammar {

    public static TokenType getWordTokenType(String value) {
        return switch (value) {
            case "true" -> KEYWORD_TRUE;
            case "false" -> TokenType.KEYWORD_FALSE;
            case "&&" -> TokenType.KEYWORD_AND;
            case "||" -> TokenType.KEYWORD_OR;
            default -> TokenType.BAD_SYNTAX_TOKEN;
        };
    }
}
