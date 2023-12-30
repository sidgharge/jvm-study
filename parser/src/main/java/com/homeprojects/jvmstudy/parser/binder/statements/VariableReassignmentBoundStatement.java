package com.homeprojects.jvmstudy.parser.binder.statements;

import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.binder.expressions.BoundExpression;

public record VariableReassignmentBoundStatement(
        Token identifierToken,
        Token equalsToken,
        BoundExpression expression,
        Token semiColonToken) implements BoundStatement {

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s %s%s",
                identifierToken.value(),
                equalsToken.value(),
                expression.toString(),
                semiColonToken.value());
    }
}
