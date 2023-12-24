package come.homeproects.jvmstudy.parser;

import come.homeproects.jvmstudy.parser.expressions.BinaryExpression;
import come.homeproects.jvmstudy.parser.expressions.Expression;
import come.homeproects.jvmstudy.parser.expressions.NumberExpression;
import come.homeproects.jvmstudy.parser.lexer.Lexer;
import come.homeproects.jvmstudy.parser.lexer.Token;
import come.homeproects.jvmstudy.parser.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final Lexer lexer;

    private final TokenPrecedence tokenPrecedence;

    private final Diagnostics diagnostics;

    private final List<Token> tokens;

    private int index;

    private final boolean debug;

    public Parser(String expression) {
        this(expression, true);
    }

    public Parser(String expression, boolean debug) {
        this.lexer = new Lexer(expression);
        this.diagnostics = new Diagnostics();
        this.tokens = new ArrayList<>();
        this.index = 0;
        this.tokenPrecedence = new TokenPrecedence();
        this.debug = debug;
    }


    public Expression parse() {
        tokenize();
        if (this.diagnostics.hasErrors()) {
            printErrors();
            return null;
        }
        printTokens();

        Expression expression = parseExpression();
        if (this.diagnostics.hasErrors()) {
            printErrors();
        }
        return expression;
    }

    private Expression parseExpression() {
        return parseExpression(0);
    }

    private Expression parseExpression(int parentPrecedence) {
        Expression left = parsePrimaryExpression();

        while (!isAtEnd()) {
            Token operatorToken = current();
            if (tokenPrecedence.precedence(operatorToken) <= parentPrecedence) {
                break;
            }
            advance();
            Expression right = parseExpression(tokenPrecedence.precedence(operatorToken));
            left = new BinaryExpression(left, right, operatorToken);
        }
        return left;
    }

    private Expression parsePrimaryExpression() {
        Token token = current();
        if (token.type() == TokenType.OPEN_BRACKET_TOKEN) {
            return parseBracketExpression();
        }
        if (token.type() == TokenType.NUMBER_TOKEN) {
            advance();
            return new NumberExpression(token);
        }
        this.diagnostics.addDiagnostic("ERROR: expected number or a bracket at index %d, got '%s'", token.startIndex(), token.value());
        return new NumberExpression(new Token("", TokenType.NUMBER_TOKEN, token.startIndex(), token.endIndex()));
    }

    private Expression parseBracketExpression() {
        advance();
        Expression expression = parseExpression();
        Token token = current();
        if (token.type() != TokenType.CLOSED_BRACKET_TOKEN) {
            diagnostics.addDiagnostic("ERROR: Expected closing bracket, got '%s' at index %d", token.value(), token.startIndex());
        }
        advance();
        return expression;
    }

    private void advance() {
        if (!isAtEnd()) {
            index++;
        }
    }

    private Token current() {
        return tokens.get(index);
    }

    private boolean isAtEnd() {
        return index >= tokens.size();
    }








    private void printTokens() {
        if (!debug) {
            return;
        }

        System.out.println("--------------- TOKENS -------------------------");
        for (Token token : this.tokens) {
            System.out.println(token);
        }
        System.out.println();
    }

    private void printErrors() {
        if (!debug) {
            return;
        }
        System.err.println("--------------- ERRORS -------------------------");
        for (String error : this.diagnostics.errors()) {
            System.err.println(error);
        }
    }

    private void tokenize() {
        Token token = null;
        while (true) {
            token = this.lexer.nextToken();
            this.tokens.add(token);
            if (token.type() == TokenType.END_OF_FILE_TOKEN) {
                break;
            }
            if (token.type() == TokenType.BAD_SYNTAX_TOKEN) {
                this.diagnostics.addDiagnostic("ERROR: Unrecognized token '%s' at index %d", token.value(), token.startIndex());
            }
        }
    }
}
