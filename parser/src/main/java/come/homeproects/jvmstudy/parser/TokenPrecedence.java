package come.homeproects.jvmstudy.parser;

import come.homeproects.jvmstudy.parser.lexer.Token;
import come.homeproects.jvmstudy.parser.lexer.TokenType;

import java.util.HashMap;
import java.util.Map;

public class TokenPrecedence {

    private final Map<TokenType, Integer> precedences;

    public TokenPrecedence() {
        int i = 0;
        precedences = new HashMap<>();

//        precedences.put(TokenType.NUMBER_TOKEN, i);
        precedences.put(TokenType.CLOSED_BRACKET_TOKEN, i);
        precedences.put(TokenType.OPEN_CURLY_BRACKET_TOKEN, i);
        precedences.put(TokenType.SEMI_COLON_TOKEN, i);
        precedences.put(TokenType.CLOSED_CURLY_BRACKET_TOKEN, i++);

        precedences.put(TokenType.EQUALS_TOKEN, i++);

        precedences.put(TokenType.DOUBLE_EQUALS_TOKEN, i);
        precedences.put(TokenType.BANG_EQUALS_TOKEN, i++);

        precedences.put(TokenType.PLUS_TOKEN, i);
        precedences.put(TokenType.MINUS_TOKEN, i);
        precedences.put(TokenType.DOUBLE_AMPERSAND_TOKEN, i);
        precedences.put(TokenType.DOUBLE_PIPE_TOKEN, i++);

        precedences.put(TokenType.START_TOKEN, i);
        precedences.put(TokenType.SLASH_TOKEN, i++);

        precedences.put(TokenType.END_OF_FILE_TOKEN, -1);
        precedences.put(TokenType.BAD_SYNTAX_TOKEN, -1);
    }

    public Integer precedence(Token token) {
        return precedences.get(token.type());
    }

    public boolean hasHigherPrecedence(Token token, int precedence) {
        return precedences.get(token.type()) < precedence;
    }

    public boolean hasHigherOrSamePrecedence(Token token, int precedence) {
        return precedences.get(token.type()) <= precedence;
    }
}
