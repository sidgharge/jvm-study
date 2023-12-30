package com.homeprojects.jvmstudy.parser.binder.expressions;

import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.types.Type;

public record BinaryBoundExpression(BoundExpression left, BoundExpression right, Token operatorToken, Type type) implements BoundExpression {

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left.toString(), operatorToken.value(), right.toString());
    }
}
