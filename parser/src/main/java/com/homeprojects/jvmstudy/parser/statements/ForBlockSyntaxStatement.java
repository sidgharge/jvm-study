package com.homeprojects.jvmstudy.parser.statements;

import com.homeprojects.jvmstudy.parser.lexer.Token;

public record ForBlockSyntaxStatement(
        Token forKeywordToken,
        Token openBracket,
        SyntaxStatement initializer,
        ExpressionSyntaxStatement condition,
        VariableReassignmentSyntaxStatement stepper,
        Token closedBracket,
        BlockSyntaxStatement forBlockBody) implements SyntaxStatement {

    @Override
    public StatementType statementType() {
        return StatementType.FOR_STATEMENT;
    }

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s%s%s%s%s\n%s",
                forKeywordToken.value(),
                openBracket.value(),
                initializer.toString(),
                condition.toString(),
                stepper.toString(),
                closedBracket.value(),
                forBlockBody.prettyString(indent));
    }
}
