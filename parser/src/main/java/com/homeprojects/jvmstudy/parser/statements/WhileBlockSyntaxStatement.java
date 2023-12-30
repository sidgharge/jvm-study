package com.homeprojects.jvmstudy.parser.statements;

import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.expressions.SyntaxExpression;

public record WhileBlockSyntaxStatement(
        Token whileKeywordToken,
        Token openBracket,
        SyntaxExpression condition,
        Token closedBracket,
        BlockSyntaxStatement whileBlockBody) implements SyntaxStatement {

    @Override
    public StatementType statementType() {
        return StatementType.WHILE_STATEMENT;
    }

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s%s%s\n%s",
                whileKeywordToken.value(),
                openBracket.value(),
                condition.toString(),
                closedBracket.value(),
                whileBlockBody.prettyString(indent));
    }
}
