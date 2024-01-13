package com.homeprojects.jvmstudy.parser.binder.statements;

import com.homeprojects.jvmstudy.parser.lexer.Token;

public record ElseBlockBoundStatement(
        Token elseKeywordToken,
        BlockBoundStatement elseBlockBody) implements BoundStatement {


    @Override
    public int startIndex() {
        return elseKeywordToken.startIndex();
    }

    @Override
    public int endIndex() {
        return elseKeywordToken.endIndex();
    }

    @Override
    public int lineNumber() {
        return elseKeywordToken.lineNumber();
    }

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return String.format(" %s\n%s",
                elseKeywordToken.value(),
                elseBlockBody.prettyString(indent));
    }
}
