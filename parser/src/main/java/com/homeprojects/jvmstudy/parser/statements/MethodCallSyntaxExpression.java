package com.homeprojects.jvmstudy.parser.statements;

import com.homeprojects.jvmstudy.parser.expressions.ArgumentsSyntax;
import com.homeprojects.jvmstudy.parser.expressions.SyntaxExpression;
import com.homeprojects.jvmstudy.parser.expressions.SyntaxExpressionType;
import com.homeprojects.jvmstudy.parser.lexer.Token;

public record MethodCallSyntaxExpression(Token methodName, Token openBrace, ArgumentsSyntax argumentsSyntax, Token closedBrace) implements SyntaxExpression {

    @Override
    public SyntaxExpressionType expressionType() {
        return SyntaxExpressionType.METHOD_CALL;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s%s",
                methodName.value(),
                openBrace.value(),
                argumentsSyntax.toString(),
                closedBrace.value());
    }
}
