package come.homeproects.jvmstudy.parser.lexer;

import static come.homeproects.jvmstudy.parser.lexer.TokenType.KEYWORD_TRUE_TOKEN;

public class Grammar {

    public static TokenType getWordTokenType(String value) {
        return switch (value) {
            case "true" -> KEYWORD_TRUE_TOKEN;
            case "false" -> TokenType.KEYWORD_FALSE_TOKEN;
            case "&&" -> TokenType.DOUBLE_AMPERSAND_TOKEN;
            case "||" -> TokenType.DOUBLE_PIPE_TOKEN;
            case "var" -> TokenType.KEYWORD_VAR_TOKEN;
            case "if" -> TokenType.KEYWORD_IF_TOKEN;
            case "else" -> TokenType.KEYWORD_ELSE_TOKEN;
            case "while" -> TokenType.KEYWORD_WHILE_TOKEN;
            case "for" -> TokenType.KEYWORD_FOR_TOKEN;
            default -> TokenType.IDENTIFIER_TOKEN;
        };
    }
}
