package come.homeproects.jvmstudy.parser.binder.statements;

import come.homeproects.jvmstudy.parser.lexer.Token;

public record ElseBlockBoundStatement(
        Token elseKeywordToken,
        BoundStatement elseBlockBody) implements BoundStatement {

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return String.format(" %s\n%s",
                elseKeywordToken.value(),
                elseBlockBody.prettyString(indent));
    }
}
