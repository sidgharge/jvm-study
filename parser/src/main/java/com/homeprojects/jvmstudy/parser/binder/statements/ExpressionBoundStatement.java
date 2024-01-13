package com.homeprojects.jvmstudy.parser.binder.statements;

import com.homeprojects.jvmstudy.parser.binder.expressions.BoundExpression;
import com.homeprojects.jvmstudy.parser.lexer.Token;

public record ExpressionBoundStatement(BoundExpression expression, Token semiColonToken) implements BoundStatement {

    @Override
    public int startIndex() {
        return expression.startIndex();
    }

    @Override
    public int endIndex() {
        return expression.endIndex();
    }

    @Override
    public int lineNumber() {
        return expression.lineNumber();
    }

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + expression.toString() + semiColonToken.value();
    }
}
