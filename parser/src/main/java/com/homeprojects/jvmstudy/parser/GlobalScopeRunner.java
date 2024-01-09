package com.homeprojects.jvmstudy.parser;

import com.homeprojects.jvmstudy.parser.binder.Binder;
import com.homeprojects.jvmstudy.parser.binder.statements.BoundStatement;
import com.homeprojects.jvmstudy.parser.diagnostics.Diagnostics;
import com.homeprojects.jvmstudy.parser.evaluator.Evaluator;
import com.homeprojects.jvmstudy.parser.lexer.Lexer;
import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.lexer.TokenType;
import com.homeprojects.jvmstudy.parser.lowerer.Lowerer;
import com.homeprojects.jvmstudy.parser.statements.BlockSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.SyntaxStatement;

import java.util.ArrayList;
import java.util.List;

public class GlobalScopeRunner {

    private final Diagnostics diagnostics;

    private List<Token> tokens;

    private BlockSyntaxStatement syntaxStatement;

    private BoundStatement boundStatement;

    private BoundStatement loweredBoundStatement;

    public GlobalScopeRunner() {
        this.diagnostics = new Diagnostics();
        this.syntaxStatement = new BlockSyntaxStatement(
                new Token("{", TokenType.OPEN_CURLY_BRACKET_TOKEN, 0, 0, 0),
                new Token("}", TokenType.CLOSED_CURLY_BRACKET_TOKEN, 0, 0, 0),
                new ArrayList<>()
        );
    }

    public Object run(String expression) {
        this.diagnostics.errors().clear();

        Lexer lexer = new Lexer(expression);
        this.tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        List<SyntaxStatement> syntaxStatements = parser.parseStatments();
        this.syntaxStatement.statements().addAll(syntaxStatements);
        diagnostics.errors().addAll(parser.diagnostics().errors());

        Binder binder = new Binder(syntaxStatement);
        this.boundStatement = binder.bind();
        diagnostics.errors().addAll(binder.diagnostics().errors());

        Lowerer lowerer = new Lowerer(boundStatement);
        this.loweredBoundStatement = lowerer.lower();

        if (this.diagnostics.hasErrors()) {
            this.syntaxStatement.statements().removeAll(syntaxStatements);
            return null;
        }

        Evaluator evaluator = new Evaluator(loweredBoundStatement);
        return evaluator.evaluate();
    }

    public Diagnostics diagnostics() {
        return diagnostics;
    }

    public List<Token> tokens() {
        return tokens;
    }

    public SyntaxStatement syntaxStatement() {
        return syntaxStatement;
    }

    public BoundStatement boundStatement() {
        return boundStatement;
    }

    public BoundStatement loweredBoundStatement() {
        return loweredBoundStatement;
    }
}
