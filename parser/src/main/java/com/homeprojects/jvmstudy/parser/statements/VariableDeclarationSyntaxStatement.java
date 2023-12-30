package com.homeprojects.jvmstudy.parser.statements;

import com.homeprojects.jvmstudy.parser.expressions.SyntaxExpression;
import com.homeprojects.jvmstudy.parser.lexer.Token;

public record VariableDeclarationSyntaxStatement(
        Token letToken,
        Token identifierToken,
        Token colonToken,
        Token typeToken,
        Token equalsToken,
        SyntaxExpression expression,
        Token semiColonToken) implements SyntaxStatement {

    @Override
    public StatementType statementType() {
        return StatementType.VARIABLE_DECLARATION_STATEMENT;
    }

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s%s %s %s %s%s",
                letToken.value(),
                identifierToken.value(),
                colonToken == null ? "" : colonToken.value(),
                typeToken == null ? "" : typeToken.value(),
                equalsToken.value(),
                expression.toString(),
                semiColonToken.value());
    }
}
