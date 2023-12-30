package com.homeprojects.jvmstudy.parser.statements;

public interface SyntaxStatement {

    StatementType statementType();

    String prettyString(int indent);
}
