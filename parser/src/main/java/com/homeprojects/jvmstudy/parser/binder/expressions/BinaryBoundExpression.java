package com.homeprojects.jvmstudy.parser.binder.expressions;

import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.types.Type;

public record BinaryBoundExpression(BoundExpression left, BoundExpression right, Token operatorToken, Type type) implements BoundExpression {

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left.toString(), operatorToken.value(), right.toString());
    }

    @Override
    public int startIndex() {
        return left.startIndex();
    }

    @Override
    public int endIndex() {
        return right.endIndex();
    }

    @Override
    public int lineNumber() {
        return left.lineNumber();
    }
}
