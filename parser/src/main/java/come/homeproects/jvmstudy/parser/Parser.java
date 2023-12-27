package come.homeproects.jvmstudy.parser;

import come.homeproects.jvmstudy.parser.diagnostics.Diagnostic;
import come.homeproects.jvmstudy.parser.diagnostics.Diagnostics;
import come.homeproects.jvmstudy.parser.expressions.BinarySyntaxExpression;
import come.homeproects.jvmstudy.parser.expressions.SyntaxExpression;
import come.homeproects.jvmstudy.parser.expressions.LiteralSyntaxExpression;
import come.homeproects.jvmstudy.parser.statements.BlockSyntaxStatement;
import come.homeproects.jvmstudy.parser.statements.ExpressionSyntaxStatement;
import come.homeproects.jvmstudy.parser.statements.SyntaxStatement;
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

    public Parser(String expression) {
        this.lexer = new Lexer(expression);
        this.diagnostics = new Diagnostics();
        this.tokens = new ArrayList<>();
        this.index = 0;
        this.tokenPrecedence = new TokenPrecedence();
    }


    public SyntaxStatement parse() {
        tokenize();
        return parseStatement();
    }

    public SyntaxStatement parseStatement() {
        Token token = current();
        return switch (token.type()) {
            case TokenType.OPEN_CURLY_BRACKET_TOKEN -> parseBlockStatement();
            default -> parseExpressionStatement();
        };
    }

    private ExpressionSyntaxStatement parseExpressionStatement() {
        SyntaxExpression expression = parseExpression();
        Token semiColonToken = matchAndAdvance(TokenType.SEMI_COLON_TOKEN);
        return new ExpressionSyntaxStatement(expression, semiColonToken);
    }

    private BlockSyntaxStatement parseBlockStatement() {
        Token openCurlyBracketToken = current();
        List<SyntaxStatement> statements = new ArrayList<>();
        advance();
        Token token = current();
        while (!token.type().equals(TokenType.CLOSED_CURLY_BRACKET_TOKEN)) {
            SyntaxStatement statement = parseStatement();
            statements.add(statement);
            token = current();
        }
        advance();
        return new BlockSyntaxStatement(openCurlyBracketToken, token, statements);
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
        return Integer.MIN_VALUE;
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
        if (token.type() == TokenType.KEYWORD_TRUE_TOKEN || token.type() ==  TokenType.KEYWORD_FALSE_TOKEN || token.type() == TokenType.IDENTIFIER_TOKEN) {
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

    private Token matchAndAdvance(TokenType type) {
        Token token = current();
        if (token.type().equals(type)) {
            advance();
            return token;
        }
        diagnostics.addDiagnostic(token, "Expected %s, got %s", type, token.type());
        return new Token("", type, token.startIndex(), token.startIndex(), token.lineNumber());
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

    private void tokenize() {
        Token token = null;
        while (true) {
            token = this.lexer.nextToken();
            this.tokens.add(token);
            if (token.type() == TokenType.END_OF_FILE_TOKEN) {
                break;
            }
        }
    }

    public Diagnostics diagnostics() {
        return diagnostics;
    }

    public void printTokens() {
        System.out.println("--------------- TOKENS -------------------------");
        for (Token token : this.tokens) {
            System.out.println(token);
        }
        System.out.println();
    }

    public void printErrors() {
        if (!this.diagnostics.hasErrors()) {
            return;
        }
        System.err.println("--------------- ERRORS -------------------------");
        for (Diagnostic diagnostic : this.diagnostics.errors()) {
            System.err.println(diagnostic.message());
        }
    }

    public List<Token> tokens() {
        return this.tokens;
    }
}
