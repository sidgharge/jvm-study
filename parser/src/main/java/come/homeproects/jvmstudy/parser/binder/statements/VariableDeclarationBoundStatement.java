package come.homeproects.jvmstudy.parser.binder.statements;

import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression;
import come.homeproects.jvmstudy.parser.lexer.Token;
import come.homeproects.jvmstudy.parser.types.Type;

public record VariableDeclarationBoundStatement(
        Token letToken,
        Token identifierToken,
        Token colonToken,
        Type type,
        Token equalsToken,
        BoundExpression expression,
        Token semiColonToken) implements BoundStatement {

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s%s %s %s %s%s",
                letToken.value(),
                identifierToken.value(),
                colonToken.value(),
                type.typeName(),
                equalsToken.value(),
                expression.toString(),
                semiColonToken.value());
    }
}
