package come.homeproects.jvmstudy.parser.binder.statements;

import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression;
import come.homeproects.jvmstudy.parser.expressions.SyntaxExpression;
import come.homeproects.jvmstudy.parser.lexer.Token;
import come.homeproects.jvmstudy.parser.statements.BlockSyntaxStatement;

public record IfBlockBoundStatement(
        Token ifKeywordToken,
        Token openBracket,
        BoundExpression condition,
        Token closedBracket,
        BoundStatement ifBlockBody) implements BoundStatement {

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s%s%s\n%s",
                ifKeywordToken.value(),
                openBracket.value(),
                condition.toString(),
                closedBracket.value(),
                ifBlockBody.prettyString(indent));
    }
}
