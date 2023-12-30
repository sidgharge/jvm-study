package com.homeprojects.jvmstudy.parser;

import com.homeprojects.jvmstudy.parser.binder.Binder;
import com.homeprojects.jvmstudy.parser.binder.statements.BoundStatement;
import com.homeprojects.jvmstudy.parser.diagnostics.Diagnostics;
import com.homeprojects.jvmstudy.parser.evaluator.Evaluator;
import com.homeprojects.jvmstudy.parser.lexer.Lexer;
import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.lowerer.Lowerer;
import com.homeprojects.jvmstudy.parser.statements.SyntaxStatement;

import java.util.List;

public class Runner {

    private final String expression;

    private final Diagnostics diagnostics;

    private List<Token> tokens;

    private SyntaxStatement syntaxStatement;

    private BoundStatement boundStatement;

    private BoundStatement loweredBoundStatement;

    public Runner(String expression) {
        this.expression = expression;
        this.diagnostics = new Diagnostics();
    }

    public Object run() {
        return run(true);
    }

    public Object run(boolean runOnError) {
        Lexer lexer = new Lexer(expression);
        this.tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        this.syntaxStatement = parser.parse();
        diagnostics.errors().addAll(parser.diagnostics().errors());

        Binder binder = new Binder(syntaxStatement);
        this.boundStatement = binder.bind();
        diagnostics.errors().addAll(binder.diagnostics().errors());

        Lowerer lowerer = new Lowerer(boundStatement);
        this.loweredBoundStatement = lowerer.lower();

        if (this.diagnostics.hasErrors() && !runOnError) {
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
