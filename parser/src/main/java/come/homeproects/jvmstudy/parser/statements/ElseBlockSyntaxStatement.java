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
        return printString(0);
    }

    @Override
    public String printString(int indent) {
        return String.format(" %s\n%s",
                elseKeywordToken.value(),
                elseBlockBody.printString(indent));
    }
}
