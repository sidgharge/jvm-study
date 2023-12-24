package come.homeproects.jvmstudy.parser.lexer;

import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;

public class Lexer {
    
    private static final char NULL_CHAR = '\0';

    private final String expression;

    private int index;

    public Lexer(String expression) {
        this.expression = expression;
        this.index = 0;
    }

    public Token nextToken() {
        char ch = peek();
        
        if (isWhitespace(ch)) {
            return tokenAfterWhiteSpace();
        }
        
        if (ch == NULL_CHAR) {
            return new Token(String.valueOf(NULL_CHAR), TokenType.END_OF_FILE_TOKEN, index, index);
        }
        
        if (isDigit(ch)) {
            return numberToken();
        }
        
        if (ch == '+') {
            return operatorToken('+', TokenType.PLUS_TOKEN);
        }
        if (ch == '-') {
            return operatorToken('-', TokenType.MINUS_TOKEN);
        }
        if (ch == '*') {
            return operatorToken('*', TokenType.MULTIPLICATION_TOKEN);
        }
        if (ch == '/') {
            return operatorToken('/', TokenType.DIVISION_TOKEN);
        }

        if (ch == '(') {
            return new Token("(", TokenType.OPEN_BRACKET_TOKEN, index, index++);
        }
        if (ch == ')') {
            return new Token(")", TokenType.CLOSED_BRACKET_TOKEN, index, index++);
        }

        Token token = new Token(String.valueOf(ch), TokenType.BAD_SYNTAX_TOKEN, index, index);
        next();
        return token;
    }

    private Token operatorToken(char value, TokenType tokenType) {
        Token token = new Token(String.valueOf(value), tokenType, index, index);
        next();
        return token;
    }

    private Token numberToken() {
        int startIndex = index;
        StringBuilder builder = new StringBuilder();
        while (isDigit(peek())) {
            builder.append(next());
        }
        return new Token(builder.toString(), TokenType.NUMBER_TOKEN, startIndex, index - 1);
    }

    private Token tokenAfterWhiteSpace() {
        while (isWhitespace(peek())) {
            next();
        }
        return nextToken();
    }

    private boolean isAtEnd() {
        return index >= expression.length();
    }
    
    private char peek() {
        return isAtEnd() ? NULL_CHAR : expression.charAt(index);
    }

    private char next() {
        return isAtEnd() ? NULL_CHAR : expression.charAt(index++);
    }
}
