package come.homeproects.jvmstudy.parser.binder.statements;

import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression;
import come.homeproects.jvmstudy.parser.lexer.Token;

public record WhileBlockBoundStatement(
        Token whileKeywordToken,
        Token openBracket,
        BoundExpression condition,
        Token closedBracket,
        BoundStatement whileBlockBody) implements BoundStatement {
    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s%s%s\n%s",
                whileKeywordToken.value(),
                openBracket.value(),
                condition.toString(),
                closedBracket.value(),
                whileBlockBody.prettyString(indent));
    }
}
