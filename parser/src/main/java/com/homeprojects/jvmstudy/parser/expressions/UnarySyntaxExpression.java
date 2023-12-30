package com.homeprojects.jvmstudy.parser.expressions;

import com.homeprojects.jvmstudy.parser.lexer.Token;

public record UnarySyntaxExpression(Token operator, SyntaxExpression syntaxExpression) implements SyntaxExpression {
    @Override
    public SyntaxExpressionType expressionType() {
        return SyntaxExpressionType.UNARY_EXPRESSION;
    }

    @Override
    public String toString() {
        return operator.value() + syntaxExpression;
    }
}
