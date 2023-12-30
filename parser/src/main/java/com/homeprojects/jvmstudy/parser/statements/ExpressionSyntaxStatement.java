package com.homeprojects.jvmstudy.parser.statements;

import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.expressions.SyntaxExpression;

public record ExpressionSyntaxStatement(SyntaxExpression expression, Token semiColonToken) implements SyntaxStatement {

    @Override
    public StatementType statementType() {
        return StatementType.EXPRESSION_STATEMENT;
    }

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + expression.toString() + semiColonToken.value();
    }
}
