package come.homeproects.jvmstudy.parser.binder.statements;

import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression;
import come.homeproects.jvmstudy.parser.lexer.Token;

public record ForBlockBoundStatement(
        Token forKeywordToken,
        Token openBracket,
        BoundStatement initializer,
        ExpressionBoundStatement condition,
        BoundStatement stepper,
        Token closedBracket,
        BlockBoundStatement forBlockBody) implements BoundStatement {

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s%s%s%s%s\n%s",
                forKeywordToken.value(),
                openBracket.value(),
                initializer.toString(),
                condition.toString(),
                stepper.toString(),
                closedBracket.value(),
                forBlockBody.prettyString(indent));
    }
}
