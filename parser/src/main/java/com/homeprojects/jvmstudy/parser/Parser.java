package com.homeprojects.jvmstudy.parser;

import com.homeprojects.jvmstudy.parser.diagnostics.Diagnostic;
import com.homeprojects.jvmstudy.parser.diagnostics.Diagnostics;
import com.homeprojects.jvmstudy.parser.expressions.SyntaxExpression;
import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.statements.BlockSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.ElseBlockSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.ExpressionSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.IfBlockSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.SyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.VariableReassignmentSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.WhileBlockSyntaxStatement;
import com.homeprojects.jvmstudy.parser.expressions.BinarySyntaxExpression;
import com.homeprojects.jvmstudy.parser.expressions.LiteralSyntaxExpression;
import com.homeprojects.jvmstudy.parser.statements.ForBlockSyntaxStatement;
import com.homeprojects.jvmstudy.parser.expressions.UnarySyntaxExpression;
import com.homeprojects.jvmstudy.parser.lexer.Lexer;
import com.homeprojects.jvmstudy.parser.lexer.TokenType;
import com.homeprojects.jvmstudy.parser.statements.VariableDeclarationSyntaxStatement;
import com.homeprojects.jvmstudy.parser.types.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        return parseTillEnd();
//        return parseStatement();
    }

    private SyntaxStatement parseTillEnd() {
        List<SyntaxStatement> statements = new ArrayList<>();
        while (true) {
            if (current().type().equals(TokenType.END_OF_FILE_TOKEN)) {
                break;
            }
            SyntaxStatement statement = parseStatement();
            statements.add(statement);
        }
        return new BlockSyntaxStatement(
                new Token("{", TokenType.OPEN_CURLY_BRACKET_TOKEN, 0, 0, 0),
                new Token("}", TokenType.CLOSED_CURLY_BRACKET_TOKEN, 0, 0, 0),
                statements
        );
    }

    public SyntaxStatement parseStatement() {
        Token token = current();
        return switch (token.type()) {
            case OPEN_CURLY_BRACKET_TOKEN -> parseBlockStatement();
            case KEYWORD_LET_TOKEN -> parseVariableDeclaration();
            case IDENTIFIER_TOKEN -> parseVariableReassignment();
            case KEYWORD_IF_TOKEN -> parseIfStatement();
            case KEYWORD_WHILE_TOKEN -> parseWhileStatement();
            case KEYWORD_FOR_TOKEN -> parseForStatement();
            default -> parseExpressionStatement();
        };
    }

    private ForBlockSyntaxStatement parseForStatement() {
        Token forKeywordToken = matchAndAdvance(TokenType.KEYWORD_FOR_TOKEN, "for");
        Token openBracket = matchAndAdvance(TokenType.OPEN_BRACKET_TOKEN, "(");
        SyntaxStatement initializer = parseStatement();
        ExpressionSyntaxStatement condition = parseExpressionStatement();

        VariableReassignmentSyntaxStatement stepper = parseVariableReassignment(false);
        Token closedBracket = matchAndAdvance(TokenType.CLOSED_BRACKET_TOKEN, ")");
        BlockSyntaxStatement forBlockBody = parseBlockStatement();

        return new ForBlockSyntaxStatement(forKeywordToken, openBracket, initializer, condition, stepper, closedBracket, forBlockBody);
    }

    private WhileBlockSyntaxStatement parseWhileStatement() {
        Token ifKeywordToken = matchAndAdvance(TokenType.KEYWORD_WHILE_TOKEN, "while");
        Token openBracket = matchAndAdvance(TokenType.OPEN_BRACKET_TOKEN, "(");
        SyntaxExpression condition = parseExpression();
        Token closedBracket = matchAndAdvance(TokenType.CLOSED_BRACKET_TOKEN, ")");
        BlockSyntaxStatement whileBlockBody = parseBlockStatement();
        return new WhileBlockSyntaxStatement(ifKeywordToken, openBracket, condition, closedBracket, whileBlockBody);
    }

    private IfBlockSyntaxStatement parseIfStatement() {
        Token ifKeywordToken = matchAndAdvance(TokenType.KEYWORD_IF_TOKEN, "if");
        Token openBracket = matchAndAdvance(TokenType.OPEN_BRACKET_TOKEN, "(");
        SyntaxExpression condition = parseExpression();
        Token closedBracket = matchAndAdvance(TokenType.CLOSED_BRACKET_TOKEN, ")");
        BlockSyntaxStatement ifBlockBody = parseBlockStatement();
        Optional<ElseBlockSyntaxStatement> elseStatement = parseElseStatement();
        return new IfBlockSyntaxStatement(ifKeywordToken, openBracket, condition, closedBracket, ifBlockBody, elseStatement);
    }

    private Optional<ElseBlockSyntaxStatement> parseElseStatement() {
        if (!current().type().equals(TokenType.KEYWORD_ELSE_TOKEN)) {
            return Optional.empty();
        }
        Token elseKeywordToken = matchAndAdvance(TokenType.KEYWORD_ELSE_TOKEN, "else");
        BlockSyntaxStatement elseBlockBody = parseBlockStatement();
        return Optional.of(new ElseBlockSyntaxStatement(elseKeywordToken, elseBlockBody));

    }

    private VariableReassignmentSyntaxStatement parseVariableReassignment() {
        return parseVariableReassignment(true);
    }

    private VariableReassignmentSyntaxStatement parseVariableReassignment(boolean isSemicolonExpected) {
        Token identifierToken = matchAndAdvance(TokenType.IDENTIFIER_TOKEN, "$dummy");
        Token equalsToken = matchAndAdvance(TokenType.EQUALS_TOKEN, "=");
        SyntaxExpression expression = parseExpression();

        Token semiColonToken = current();
        if (semiColonToken.type().equals(TokenType.SEMI_COLON_TOKEN) || isSemicolonExpected) {
            matchAndAdvance(TokenType.SEMI_COLON_TOKEN, ";");
        }
        return new VariableReassignmentSyntaxStatement(identifierToken, equalsToken, expression, semiColonToken);
    }

    private SyntaxStatement parseVariableDeclaration() {
        Token letToken = matchAndAdvance(TokenType.KEYWORD_LET_TOKEN, "let");
        Token identifierToken = matchAndAdvance(TokenType.IDENTIFIER_TOKEN, "$dummy");

        Token colonToken = null;
        Token typeToken = null;
        Token token = current();
        if (token.type() == TokenType.COLON_TOKEN) {
            colonToken = matchAndAdvance(TokenType.COLON_TOKEN, ":");
            typeToken = matchAndAdvance(TokenType.IDENTIFIER_TOKEN, Type.UNKNOWN.name());
        }

        Token equalsToken = matchAndAdvance(TokenType.EQUALS_TOKEN, "=");
        SyntaxExpression expression = parseExpression();
        Token semiColonToken = matchAndAdvance(TokenType.SEMI_COLON_TOKEN, ";");
        return new VariableDeclarationSyntaxStatement(letToken, identifierToken, colonToken, typeToken, equalsToken, expression, semiColonToken);
    }

    private ExpressionSyntaxStatement parseExpressionStatement() {
        SyntaxExpression expression = parseExpression();
        Token semiColonToken = matchAndAdvance(TokenType.SEMI_COLON_TOKEN, ";");
        return new ExpressionSyntaxStatement(expression, semiColonToken);
    }

    private BlockSyntaxStatement parseBlockStatement() {
        Token openCurlyBracketToken = matchAndAdvance(TokenType.OPEN_CURLY_BRACKET_TOKEN, "{");
        List<SyntaxStatement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            if (current().type().equals(TokenType.CLOSED_CURLY_BRACKET_TOKEN)) {
                break;
            }
            SyntaxStatement statement = parseStatement();
            statements.add(statement);
        }
        Token closedCurlyBracketToken = matchAndAdvance(TokenType.CLOSED_CURLY_BRACKET_TOKEN, "}");
        return new BlockSyntaxStatement(openCurlyBracketToken, closedCurlyBracketToken, statements);
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
        diagnostics.addDiagnostic(token, "Invalid token: '%s'", token.value());
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
        return new LiteralSyntaxExpression(new Token(token.value(), TokenType.BAD_SYNTAX_TOKEN, token.startIndex(), token.endIndex(), token.lineNumber()));
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

    private Token matchAndAdvance(TokenType type, String defaultValue) {
        Token token = current();
        if (token.type().equals(type)) {
            advance();
            return token;
        }
        diagnostics.addDiagnostic(token, "Expected %s, got %s", type, token.type());
        return new Token(defaultValue, type, token.startIndex(), token.startIndex(), token.lineNumber());
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
        return index >= tokens.size() - 1;
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