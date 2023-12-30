package com.homeprojects.jvmstudy.parser.statements;

import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.expressions.SyntaxExpression;

public record VariableReassignmentSyntaxStatement(
        Token identifierToken,
        Token equalsToken,
        SyntaxExpression expression,
        Token semiColonToken) implements SyntaxStatement {

    @Override
    public StatementType statementType() {
        return StatementType.VARIABLE_REASSIGNMENT_STATEMENT;
    }

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s %s%s",
                identifierToken.value(),
                equalsToken.value(),
                expression.toString(),
                semiColonToken.value());
    }
}
