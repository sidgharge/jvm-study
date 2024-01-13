package com.homeprojects.jvmstudy.parser.binder.statements;

import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.binder.expressions.BoundExpression;

import java.util.Optional;

public record IfBlockBoundStatement(
        Token ifKeywordToken,
        Token openBracket,
        BoundExpression condition,
        Token closedBracket,
        BlockBoundStatement ifBlockBody,
        Optional<ElseBlockBoundStatement> elseBlockBody) implements BoundStatement {

    @Override
    public int startIndex() {
        return ifKeywordToken.startIndex();
    }

    @Override
    public int endIndex() {
        return ifKeywordToken.endIndex();
    }

    @Override
    public int lineNumber() {
        return ifKeywordToken.lineNumber();
    }

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s%s%s\n%s%s",
                ifKeywordToken.value(),
                openBracket.value(),
                condition.toString(),
                closedBracket.value(),
                ifBlockBody.prettyString(indent),
                elseBlockBody.map(e -> e.prettyString(indent)).orElse(""));
    }
}
