package com.homeprojects.jvmstudy.parser.binder.statements;

import com.homeprojects.jvmstudy.parser.binder.expressions.BoundExpression;
import com.homeprojects.jvmstudy.parser.lexer.Token;

public record ReturnBoundStatement(Token returnToken, BoundExpression expression, Token semiColonToken) implements BoundStatement {

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return String.format("%s%s %s%s",
                "  ".repeat(indent),
                returnToken.value(),
                expression.toString(),
                semiColonToken.value());
    }
}
