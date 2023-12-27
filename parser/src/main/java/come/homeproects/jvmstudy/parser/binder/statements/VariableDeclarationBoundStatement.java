package come.homeproects.jvmstudy.parser.binder.statements;

import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression;
import come.homeproects.jvmstudy.parser.lexer.Token;

public record VariableDeclarationBoundStatement(
        Token varToken,
        Token identifierToken,
        Token equalsToken,
        BoundExpression expression,
        Token semiColonToken) implements BoundStatement {

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s %s %s%s",
                varToken.value(),
                identifierToken.value(),
                equalsToken.value(),
                expression.toString(),
                semiColonToken.value());
    }
}
