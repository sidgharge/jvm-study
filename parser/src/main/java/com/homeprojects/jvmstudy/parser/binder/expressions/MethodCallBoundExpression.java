package com.homeprojects.jvmstudy.parser.binder.expressions;

import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.types.Type;

public record MethodCallBoundExpression(Token methodName, Token openBrace, ArgumentsBound arguments, Token closedBrace, Type type) implements BoundExpression {

    @Override
    public String toString() {
        return String.format("(%s%s%s%s: %s)",
                methodName.value(),
                openBrace.value(),
                arguments.toString(),
                closedBrace.value(),
                type.typeName());
    }
}
