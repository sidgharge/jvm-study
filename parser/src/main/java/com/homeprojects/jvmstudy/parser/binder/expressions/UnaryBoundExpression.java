package com.homeprojects.jvmstudy.parser.binder.expressions;

import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.types.Type;

public record UnaryBoundExpression(Token operatorToken, Type type, BoundExpression operand) implements BoundExpression {

    @Override
    public String toString() {
        return String.format("%s%s", operatorToken.value(), operand.toString());
    }

    @Override
    public int startIndex() {
        return operatorToken.startIndex();
    }

    @Override
    public int endIndex() {
        return operand.endIndex();
    }

    @Override
    public int lineNumber() {
        return operatorToken.lineNumber();
    }
}
