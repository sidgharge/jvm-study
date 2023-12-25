package come.homeproects.jvmstudy.parser.lexer;

import static java.lang.Character.isAlphabetic;
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
            return operatorToken('*', TokenType.START_TOKEN);
        }
        if (ch == '/') {
            return operatorToken('/', TokenType.SLASH_TOKEN);
        }

        if (ch == '(') {
            return new Token("(", TokenType.OPEN_BRACKET_TOKEN, index, index++);
        }
        if (ch == ')') {
            return new Token(")", TokenType.CLOSED_BRACKET_TOKEN, index, index++);
        }

        if (ch == '&' || ch == '|') {
            return logicalOperator();
        }

        if (ch == '=' || ch == '!') {
            return bangOrEqualsRelatedOperator();
        }

        if (isAlphabetic(ch)) {
            return wordToken();
        }



        Token token = new Token(String.valueOf(ch), TokenType.BAD_SYNTAX_TOKEN, index, index);
        currentAndAdvance();
        return token;
    }

    private Token bangOrEqualsRelatedOperator() {
        char ch1 = currentAndAdvance();
        char ch2 = currentAndAdvance();
        if (ch1 == '=' && ch2 == '=') {
            return new Token("==", TokenType.DOUBLE_EQUALS_TOKEN, index - 1, index++);
        }
        if (ch1 == '!' && ch2 == '=') {
            return new Token("!=", TokenType.BANG_EQUALS_TOKEN, index - 1, index++);
        }
        if (ch1 == '!' && ch2 != '=') {
            index--;
            return new Token("!", TokenType.BANG_TOKEN, index - 1, index - 1);
        }
        return new Token("", TokenType.BAD_SYNTAX_TOKEN, index - 1, index++);
    }

    private Token logicalOperator() {
        if (peek() == '&' && currentAndAdvance() == '&') {
            return new Token("&&", TokenType.DOUBLE_AMPERSAND_TOKEN, index - 1, index++);
        }
        if (peek() == '|' && currentAndAdvance() == '|') {
            return new Token("||", TokenType.DOUBLE_PIPE_TOKEN, index - 1, index++);
        }
        return new Token("", TokenType.BAD_SYNTAX_TOKEN, index, index++);
    }

    private Token wordToken() {
        int start = index;
        StringBuilder builder = new StringBuilder();
        while (!isAtEnd()) {
            char ch = peek();
            if (ch == NULL_CHAR) {
                break;
            }
            if (!isAlphabetic(ch)) {
                break;
            }
            builder.append(ch);
            index++;
        }
        String value = builder.toString();
        TokenType tokenType = Grammar.getWordTokenType(value);
        return new Token(value, tokenType, start, index - 1);
    }

    private Token operatorToken(char value, TokenType tokenType) {
        Token token = new Token(String.valueOf(value), tokenType, index, index);
        currentAndAdvance();
        return token;
    }

    private Token numberToken() {
        int startIndex = index;
        StringBuilder builder = new StringBuilder();
        while (isDigit(peek())) {
            builder.append(currentAndAdvance());
        }
        return new Token(builder.toString(), TokenType.NUMBER_TOKEN, startIndex, index - 1);
    }

    private Token tokenAfterWhiteSpace() {
        while (isWhitespace(peek())) {
            currentAndAdvance();
        }
        return nextToken();
    }

    private boolean isAtEnd() {
        return index >= expression.length();
    }
    
    private char peek() {
        return isAtEnd() ? NULL_CHAR : expression.charAt(index);
    }

    private char currentAndAdvance() {
        return isAtEnd() ? NULL_CHAR : expression.charAt(index++);
    }
}
