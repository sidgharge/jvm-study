package com.homeprojects.jvmstudy.parser.binder.statements;

import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.binder.expressions.BoundExpression;

public record WhileBlockBoundStatement(
        Token whileKeywordToken,
        Token openBracket,
        BoundExpression condition,
        Token closedBracket,
        BlockBoundStatement whileBlockBody) implements BoundStatement {

    @Override
    public int startIndex() {
        return whileKeywordToken.startIndex();
    }

    @Override
    public int endIndex() {
        return whileKeywordToken.endIndex();
    }

    @Override
    public int lineNumber() {
        return whileKeywordToken.lineNumber();
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
