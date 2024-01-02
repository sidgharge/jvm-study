package com.homeprojects.jvmstudy.parser.binder.expressions;

import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.types.Type;

public record ArgumentBoundExpression(BoundExpression expression, Token commaToken) implements BoundExpression {

    @Override
    public Type type() {
        return expression.type();
    }

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
        return String.format("(%s: %s)%s", expression.toString(), type().typeName(), commaToken == null ? "" : commaToken.value());
    }
}
