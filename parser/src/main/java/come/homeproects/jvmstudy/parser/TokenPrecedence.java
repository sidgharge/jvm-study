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

        precendences.put(TokenType.NUMBER_TOKEN, i);
        precendences.put(TokenType.CLOSED_BRACKET_TOKEN, i++);

        precendences.put(TokenType.DOUBLE_EQUALS_TOKEN, i);
        precendences.put(TokenType.BANG_EQUALS_TOKEN, i++);

        precendences.put(TokenType.PLUS_TOKEN, i);
        precendences.put(TokenType.MINUS_TOKEN, i);
        precendences.put(TokenType.DOUBLE_AMPERSAND_TOKEN, i);
        precendences.put(TokenType.DOUBLE_PIPE_TOKEN, i++);

        precendences.put(TokenType.START_TOKEN, i);
        precendences.put(TokenType.SLASH_TOKEN, i++);


        precendences.put(TokenType.END_OF_FILE_TOKEN, -1);
        precendences.put(TokenType.BAD_SYNTAX_TOKEN, -1);
    }

    public Integer precedence(Token token) {
        return precendences.get(token.type());
    }

    public boolean hasHigherPrecedence(Token token, int precedence) {
        return precendences.get(token.type()) < precedence;
    }

    public boolean hasHigherOrSamePrecedence(Token token, int precedence) {
        return precendences.get(token.type()) <= precedence;
    }
}
