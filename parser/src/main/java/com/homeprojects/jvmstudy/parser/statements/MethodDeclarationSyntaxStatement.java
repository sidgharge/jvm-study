package com.homeprojects.jvmstudy.parser.statements;

import com.homeprojects.jvmstudy.parser.lexer.Token;

public record MethodDeclarationSyntaxStatement(
        Token methodNameToken,
        Token openBracketToken,
        ParametersSyntax parameters,
        Token closedBracketToken,
        BlockSyntaxStatement methodBody,
        Token colonToken,
        Token returnTypeToken) implements SyntaxStatement {

    @Override
    public StatementType statementType() {
        return StatementType.METHOD_DECLARATION_STATEMENT;
    }

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return String.format("%s%s%s%s%s%s %s\n%s",
                "  ".repeat(indent),
                methodNameToken.value(),
                openBracketToken.value(),
                parameters.toString(),
                closedBracketToken.value(),
                colonToken.value(),
                returnTypeToken.value(),
                methodBody.prettyString(indent));
    }
}
