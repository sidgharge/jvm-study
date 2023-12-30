package com.homeprojects.jvmstudy.parser.expressions;

import com.homeprojects.jvmstudy.parser.expressions.SyntaxExpression;
import com.homeprojects.jvmstudy.parser.lexer.Token;

public record ArgumentSyntax(SyntaxExpression expression, Token commaToken) {

    @Override
    public String toString() {
        return String.format("%s%s", expression.toString(), commaToken == null ? "" : commaToken.value());
    }
}
