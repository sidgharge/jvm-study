package come.homeproects.jvmstudy.parser.statements;

import come.homeproects.jvmstudy.parser.expressions.SyntaxExpression;
import come.homeproects.jvmstudy.parser.lexer.Token;

public record WhileBlockSyntaxStatement(
        Token whileKeywordToken,
        Token openBracket,
        SyntaxExpression condition,
        Token closedBracket,
        BlockSyntaxStatement whileBlockBody) implements SyntaxStatement {

    @Override
    public StatementType statementType() {
        return StatementType.WHILE_STATEMENT;
    }

    @Override
    public String toString() {
        return printString(0);
    }

    @Override
    public String printString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s%s%s\n%s",
                whileKeywordToken.value(),
                openBracket.value(),
                condition.toString(),
                closedBracket.value(),
                whileBlockBody.printString(indent));
    }
}
