package come.homeproects.jvmstudy.parser.binder.statements;

import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression;
import come.homeproects.jvmstudy.parser.lexer.Token;

import java.util.Optional;

public record IfBlockBoundStatement(
        Token ifKeywordToken,
        Token openBracket,
        BoundExpression condition,
        Token closedBracket,
        BoundStatement ifBlockBody,
        Optional<ElseBlockBoundStatement> elseBlockBody) implements BoundStatement {

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s%s%s\n%s%s",
                ifKeywordToken.value(),
                openBracket.value(),
                condition.toString(),
                closedBracket.value(),
                ifBlockBody.prettyString(indent),
                elseBlockBody.map(e -> e.prettyString(indent)).orElse(""));
    }
}
