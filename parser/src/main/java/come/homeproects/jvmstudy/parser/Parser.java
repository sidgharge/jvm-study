package come.homeproects.jvmstudy.parser;

import come.homeproects.jvmstudy.parser.diagnostics.Diagnostic;
import come.homeproects.jvmstudy.parser.diagnostics.Diagnostics;
import come.homeproects.jvmstudy.parser.expressions.BinarySyntaxExpression;
import come.homeproects.jvmstudy.parser.expressions.SyntaxExpression;
import come.homeproects.jvmstudy.parser.expressions.LiteralSyntaxExpression;
import come.homeproects.jvmstudy.parser.expressions.UnarySyntaxExpression;
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


    public SyntaxExpression parse() {
        tokenize();
        if (this.diagnostics.hasErrors()) {
            printErrors();
        }
        printTokens();

        SyntaxExpression syntaxExpression = parseExpression();
        if (this.diagnostics.hasErrors()) {
            printErrors();
        }
        return syntaxExpression;
    }

    private SyntaxExpression parseExpression() {
        return parseExpression(0);
    }

    private SyntaxExpression parseExpression(int parentPrecedence) {
        SyntaxExpression left = parsePrimaryExpression();

        while (!isAtEnd()) {
            Token operatorToken = current();
            int precedence = getPrecedence(operatorToken);
            if (precedence <= parentPrecedence) {
                break;
            }
            advance();
            SyntaxExpression right = parseExpression(precedence);
            left = new BinarySyntaxExpression(left, right, operatorToken);
        }
        return left;
    }

    private int getPrecedence(Token token) {
        Integer precedence = tokenPrecedence.precedence(token);
        if (precedence != null) {
            return precedence;
        }
        diagnostics.addDiagnostic(token, "Precedence is not defined for: %s", token.type());
        return Integer.MAX_VALUE;
    }

    private SyntaxExpression parsePrimaryExpression() {
        Token token = current();
        advance();
        return tokenToExpression(token);
    }

    private SyntaxExpression tokenToExpression(Token token) {
        if (token.type() == TokenType.OPEN_BRACKET_TOKEN) {
            return parseBracketExpression();
        }
        if (token.type() == TokenType.NUMBER_TOKEN) {
            return new LiteralSyntaxExpression(token);
        }
        if (token.type() == TokenType.PLUS_TOKEN || token.type() == TokenType.MINUS_TOKEN) {
            SyntaxExpression syntaxExpression = parsePrimaryExpression();
            return new UnarySyntaxExpression(token, syntaxExpression);
        }
        if (token.type() == TokenType.BANG_TOKEN) {
            SyntaxExpression syntaxExpression = parsePrimaryExpression();
            return new UnarySyntaxExpression(token, syntaxExpression);
        }
        if (token.type() == TokenType.KEYWORD_TRUE_TOKEN || token.type() ==  TokenType.KEYWORD_FALSE_TOKEN) {
            return new LiteralSyntaxExpression(token);
        }
        this.diagnostics.addDiagnostic(token, "Invalid token: '%s'", token.value());
        return new LiteralSyntaxExpression(new Token("", TokenType.NUMBER_TOKEN, token.startIndex(), token.endIndex(), token.lineNumber()));
    }

    private SyntaxExpression parseBracketExpression() {
        SyntaxExpression syntaxExpression = parseExpression();
        Token token = current();
        if (token.type() != TokenType.CLOSED_BRACKET_TOKEN && tokens.get(index - 1).type() != TokenType.CLOSED_BRACKET_TOKEN) {
            diagnostics.addDiagnostic(token, "Expected closing bracket");
        }
        advance();
        return syntaxExpression;
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
        for (Diagnostic diagnostic : this.diagnostics.errors()) {
            System.err.println(diagnostic.message());
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
                this.diagnostics.addDiagnostic(token, "Unrecognized token");
            }
        }
    }

    public Diagnostics diagnostics() {
        return diagnostics;
    }
}
