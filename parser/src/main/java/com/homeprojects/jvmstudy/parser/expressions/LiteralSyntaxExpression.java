package com.homeprojects.jvmstudy.parser.expressions;

import com.homeprojects.jvmstudy.parser.lexer.Token;

public record LiteralSyntaxExpression(Token token) implements SyntaxExpression {

    @Override
    public SyntaxExpressionType expressionType() {
        return SyntaxExpressionType.LITERAL_EXPRESSION;
    }

    @Override
    public String toString() {
        return token.value();
    }
}
