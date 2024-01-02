package com.homeprojects.jvmstudy.parser.statements;

import com.homeprojects.jvmstudy.parser.expressions.SyntaxExpression;
import com.homeprojects.jvmstudy.parser.lexer.Token;

public record ReturnSyntaxStatement(Token returnToken, SyntaxExpression expression, Token semiColonToken) implements SyntaxStatement {

    @Override
    public StatementType statementType() {
        return StatementType.RETURN_STATEMENT;
    }

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return String.format("%s%s %s%s",
                "  ".repeat(indent),
                returnToken.value(),
                expression.toString(),
                semiColonToken.value());
    }
}
