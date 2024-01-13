package com.homeprojects.jvmstudy.parser.binder.statements;

import com.homeprojects.jvmstudy.parser.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

public record BlockBoundStatement(Token openBrace, Token closedBrace, List<BoundStatement> statements) implements BoundStatement {

    @Override
    public int startIndex() {
        return openBrace.startIndex();
    }

    @Override
    public int endIndex() {
        return openBrace.endIndex();
    }

    @Override
    public int lineNumber() {
        return openBrace.lineNumber();
    }

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        String tabs = "  ".repeat(indent);
        return statements.stream()
                .map(s -> s.prettyString(indent + 1))
                .collect(Collectors.joining("\n", tabs + "{\n", "\n" + tabs + "}"));
    }

}
