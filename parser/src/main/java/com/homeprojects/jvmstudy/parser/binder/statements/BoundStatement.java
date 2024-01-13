package com.homeprojects.jvmstudy.parser.binder.statements;

public interface BoundStatement {

    String prettyString(int indent);

    int startIndex();

    int endIndex();

    int lineNumber();
}
