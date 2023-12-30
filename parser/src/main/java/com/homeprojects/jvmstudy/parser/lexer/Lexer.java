package com.homeprojects.jvmstudy.parser.lexer;

import com.homeprojects.jvmstudy.parser.SourceText;

import static java.lang.Character.isAlphabetic;
import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;

public class Lexer {
    
    private static final char NULL_CHAR = '\0';

    private final SourceText text;

    public Lexer(String expression) {
        this.text = new SourceText(expression);
    }

    public Token nextToken() {
        char ch = text.peek();
        
        if (isWhitespace(ch)) {
            return tokenAfterWhiteSpace();
        }
        
        if (ch == NULL_CHAR) {
            return singleCharacterToken(String.valueOf(NULL_CHAR), TokenType.END_OF_FILE_TOKEN);
        }
        
        if (isDigit(ch)) {
            return numberToken();
        }
        
        if (ch == '+') {
            return singleCharacterToken("+", TokenType.PLUS_TOKEN);
        }
        if (ch == '-') {
            return singleCharacterToken("-", TokenType.MINUS_TOKEN);
        }
        if (ch == '*') {
            return singleCharacterToken("*", TokenType.START_TOKEN);
        }
        if (ch == '/') {
            return singleCharacterToken("/", TokenType.SLASH_TOKEN);
        }

        if (ch == '(') {
            return singleCharacterToken("(", TokenType.OPEN_BRACKET_TOKEN);
        }
        if (ch == ')') {
            return singleCharacterToken(")", TokenType.CLOSED_BRACKET_TOKEN);
        }
        if (ch == '{') {
            return singleCharacterToken("{", TokenType.OPEN_CURLY_BRACKET_TOKEN);
        }
        if (ch == '}') {
            return singleCharacterToken("}", TokenType.CLOSED_CURLY_BRACKET_TOKEN);
        }
        if (ch == ';') {
            return singleCharacterToken(";", TokenType.SEMI_COLON_TOKEN);
        }
        if (ch == ':') {
            return singleCharacterToken(":", TokenType.COLON_TOKEN);
        }

        if (ch == '&' || ch == '|') {
            return logicalOperator();
        }

        if (ch == '=' || ch == '!') {
            return bangOrEqualsRelatedOperator();
        }

        if (ch == '>' || ch == '<') {
            return compareOperator();
        }

        if (ch == '"')
            return stringToken();

        if (isAlphabetic(ch)) {
            return wordToken();
        }

        return singleCharacterToken(String.valueOf(ch), TokenType.BAD_SYNTAX_TOKEN);
    }

    private Token stringToken() {
        int startIndex = text.index();
        int lineNumber = text.lineNumber();
        StringBuilder builder = new StringBuilder();
        text.currentAndAdvance();
        while (true) {
            char ch = text.currentAndAdvance();
            if (text.isAtEnd()) {
                return new Token(builder.toString(), TokenType.BAD_SYNTAX_TOKEN, startIndex, text.index(), lineNumber);
            }
            if (ch == '"') {
                break;
            }
            builder.append(ch);
        }
        String value = builder.toString();
        return new Token(value, TokenType.STRING_TOKEN, startIndex, text.index(), lineNumber);
    }

    private Token compareOperator() {
        int lineNumber = text.lineNumber();
        int startIndex = text.index();
        char ch1 = text.currentAndAdvance();
        char ch2 = text.peek();

        if (ch1 == '>' && ch2 == '=') {
            text.currentAndAdvance();
            return new Token(">=", TokenType.GREATER_THAN_EQUALS_TOKEN, startIndex, startIndex + 2, lineNumber);
        }
        if (ch1 == '>' && ch2 != '=') {
            return new Token(">", TokenType.GREATER_THAN_TOKEN, startIndex, startIndex, text.lineNumber());
        }
        if (ch1 == '<' && ch2 == '=') {
            text.currentAndAdvance();
            return new Token("<=", TokenType.LESS_THAN_EQUALS_TOKEN, startIndex, startIndex + 2, lineNumber);
        }
        if (ch1 == '<' && ch2 != '=') {
            return new Token("<", TokenType.LESS_THAN_TOKEN, startIndex, startIndex, text.lineNumber());
        }
        return new Token(String.valueOf(ch1), TokenType.BAD_SYNTAX_TOKEN, startIndex, startIndex, lineNumber);
    }

    private Token singleCharacterToken(String value, TokenType type) {
        Token token = new Token(value, type, text.index(), text.index(), text.lineNumber());
        text.currentAndAdvance();
        return token;
    }

    private Token bangOrEqualsRelatedOperator() {
        int lineNumber = text.lineNumber();
        int startIndex = text.index();
        char ch1 = text.currentAndAdvance();
        char ch2 = text.peek();
        if (ch1 == '=' && ch2 == '=') {
            text.currentAndAdvance();
            return new Token("==", TokenType.DOUBLE_EQUALS_TOKEN, startIndex, startIndex + 2, lineNumber);
        }
        if (ch1 == '!' && ch2 == '=') {
            text.currentAndAdvance();
            return new Token("!=", TokenType.BANG_EQUALS_TOKEN, startIndex, startIndex + 2, lineNumber);
        }
        if (ch1 == '!' && ch2 != '=') {
            return new Token("!", TokenType.BANG_TOKEN, startIndex, startIndex, text.lineNumber());
        }
        if (ch1 == '=' && ch2 != '=') {
            return new Token("=", TokenType.EQUALS_TOKEN, startIndex, startIndex, text.lineNumber());
        }
        return new Token(String.valueOf(ch1), TokenType.BAD_SYNTAX_TOKEN, startIndex, startIndex, lineNumber);
    }

    private Token logicalOperator() {
        int lineNumber = text.lineNumber();
        int startIndex = text.index();
        if (text.peek() == '&' && text.currentAndAdvance() == '&') {
            text.currentAndAdvance();
            return new Token("&&", TokenType.DOUBLE_AMPERSAND_TOKEN, startIndex, startIndex + 1, lineNumber);
        }
        if (text.peek() == '|' && text.currentAndAdvance() == '|') {
            text.currentAndAdvance();
            return new Token("||", TokenType.DOUBLE_PIPE_TOKEN, startIndex, startIndex + 1, lineNumber);
        }
        return new Token("", TokenType.BAD_SYNTAX_TOKEN, startIndex, startIndex + 1, lineNumber);
    }

    private Token wordToken() {
        int start = text.index();
        int lineNumber = text.lineNumber();
        StringBuilder builder = new StringBuilder();
        while (!text.isAtEnd()) {
            char ch = text.peek();
            if (ch == NULL_CHAR) {
                break;
            }
            if (!isAlphabetic(ch)) {
                break;
            }
            builder.append(ch);
            text.currentAndAdvance();
        }
        String value = builder.toString();
        TokenType tokenType = Grammar.getWordTokenType(value);
        return new Token(value, tokenType, start, text.index() - 1, lineNumber);
    }

    private Token numberToken() {
        int startIndex = text.index();
        int lineNumber = text.lineNumber();
        StringBuilder builder = new StringBuilder();
        while (isDigit(text.peek())) {
            builder.append(text.currentAndAdvance());
        }
        return new Token(builder.toString(), TokenType.NUMBER_TOKEN, startIndex, text.index() - 1, lineNumber);
    }

    private Token tokenAfterWhiteSpace() {
        while (isWhitespace(text.peek())) {
            text.currentAndAdvance();
        }
        return nextToken();
    }

}
