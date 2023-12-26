package come.homeproects.jvmstudy.parser.lexer;

import static come.homeproects.jvmstudy.parser.lexer.TokenType.KEYWORD_TRUE_TOKEN;

public class Grammar {

    public static TokenType getWordTokenType(String value) {
        return switch (value) {
            case "true" -> KEYWORD_TRUE_TOKEN;
            case "false" -> TokenType.KEYWORD_FALSE_TOKEN;
            case "&&" -> TokenType.DOUBLE_AMPERSAND_TOKEN;
            case "||" -> TokenType.DOUBLE_PIPE_TOKEN;
            default -> TokenType.IDENTIFIER_TOKEN;
        };
    }
}
