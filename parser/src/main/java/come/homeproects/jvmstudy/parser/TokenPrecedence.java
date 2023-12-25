package come.homeproects.jvmstudy.parser;

import come.homeproects.jvmstudy.parser.lexer.Token;
import come.homeproects.jvmstudy.parser.lexer.TokenType;

import java.util.HashMap;
import java.util.Map;

public class TokenPrecedence {

    private final Map<TokenType, Integer> precendences;

    public TokenPrecedence() {
        int i = 0;
        precendences = new HashMap<>();

        precendences.put(TokenType.NUMBER_TOKEN, i++);

        precendences.put(TokenType.DOUBLE_EQUALS_TOKEN, i++);

        precendences.put(TokenType.PLUS_TOKEN, i);
        precendences.put(TokenType.MINUS_TOKEN, i);
        precendences.put(TokenType.DOUBLE_AMPERSAND_TOKEN, i);
        precendences.put(TokenType.DOUBLE_PIPE_TOKEN, i++);

        precendences.put(TokenType.START_TOKEN, i);
        precendences.put(TokenType.SLASH_TOKEN, i++);


        precendences.put(TokenType.END_OF_FILE_TOKEN, -1);
        precendences.put(TokenType.BAD_SYNTAX_TOKEN, -1);
    }

    public boolean hasHigherPrecedence(Token token1, Token token2) {
        int priority1 = precendences.get(token1.type());
        int priority2 = precendences.get(token2.type());
        if (priority1 < priority2) {
            return true;
        }
        return false;
    }

    public int precedence(Token token) {
        return precendences.getOrDefault(token.type(), 0);
    }

    public boolean hasHigherPrecedence(Token token, int precedence) {
        return precendences.get(token.type()) < precedence;
    }

    public boolean hasHigherOrSamePrecedence(Token token, int precedence) {
        return precendences.get(token.type()) <= precedence;
    }
}
