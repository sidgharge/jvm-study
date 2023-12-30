package come.homeproects.jvmstudy.parser.statements;

import come.homeproects.jvmstudy.parser.lexer.Token;

public record ElseBlockSyntaxStatement(
        Token elseKeywordToken,
        BlockSyntaxStatement elseBlockBody) implements SyntaxStatement {

    @Override
    public StatementType statementType() {
        return StatementType.ELSE_STATEMENT;
    }

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
