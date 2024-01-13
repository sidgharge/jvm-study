package com.homeprojects.jvmstudy.parser.lowerer;

import com.homeprojects.jvmstudy.parser.binder.expressions.BoundExpression;
import com.homeprojects.jvmstudy.parser.binder.statements.BoundStatement;

public record ConditionalGotoBoundStatement(BoundExpression condition, Label otherwiseLabel) implements BoundStatement {

    @Override
    public int startIndex() {
        return condition.startIndex();
    }

    @Override
    public int endIndex() {
        return condition.endIndex();
    }

    @Override
    public int lineNumber() {
        return condition().lineNumber();
    }

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return String.format("%sif %s else goto %s", "  ".repeat(indent), condition.toString(), otherwiseLabel.toString());
    }
}
