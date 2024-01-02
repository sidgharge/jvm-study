package com.homeprojects.jvmstudy.parser.binder.expressions;

import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.types.Type;

public record LiteralBoundExpression(Token token, Type type) implements BoundExpression {

    @Override
    public String toString() {
        return token.value();
    }

    @Override
    public int startIndex() {
        return token.startIndex();
    }

    @Override
    public int endIndex() {
        return token.endIndex();
    }

    @Override
    public int lineNumber() {
        return token.lineNumber();
    }
}
