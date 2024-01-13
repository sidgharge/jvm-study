package com.homeprojects.jvmstudy.parser.binder.statements;

import com.homeprojects.jvmstudy.parser.binder.expressions.BoundExpression;
import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.types.Type;

public record VariableDeclarationBoundStatement(
        Token letToken,
        Token identifierToken,
        Token colonToken,
        Type type,
        Token equalsToken,
        BoundExpression expression,
        Token semiColonToken) implements BoundStatement {

    @Override
    public int startIndex() {
        return letToken.startIndex();
    }

    @Override
    public int endIndex() {
        return letToken.endIndex();
    }

    @Override
    public int lineNumber() {
        return letToken.lineNumber();
    }

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s%s %s %s %s%s",
                letToken.value(),
                identifierToken.value(),
                colonToken.value(),
                type.typeName(),
                equalsToken.value(),
                expression.toString(),
                semiColonToken.value());
    }
}
