package come.homeproects.jvmstudy.parser.statements;

import come.homeproects.jvmstudy.parser.lexer.Token;

public record ForBlockSyntaxStatement(
        Token forKeywordToken,
        Token openBracket,
        SyntaxStatement initializer,
        ExpressionSyntaxStatement condition,
        VariableReassignmentSyntaxStatement stepper,
        Token closedBracket,
        BlockSyntaxStatement forBlockBody) implements SyntaxStatement {

    @Override
    public StatementType statementType() {
        return StatementType.FOR_STATEMENT;
    }

    @Override
    public String toString() {
        return printString(0);
    }

    @Override
    public String printString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s%s%s%s%s\n%s",
                forKeywordToken.value(),
                openBracket.value(),
                initializer.toString(),
                condition.toString(),
                stepper.toString(),
                closedBracket.value(),
                forBlockBody.printString(indent));
    }
}
