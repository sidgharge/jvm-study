package come.homeproects.jvmstudy.parser.statements;

import come.homeproects.jvmstudy.parser.expressions.SyntaxExpression;

public record ExpressionSyntaxStatement(SyntaxExpression expression) implements SyntaxStatement {

    @Override
    public StatementType statementType() {
        return StatementType.EXPRESSION_STATEMENT;
    }

    @Override
    public String toString() {
        return printString(0);
    }

    @Override
    public String printString(int indent) {
        return "  ".repeat(indent) + expression.toString();
    }
}
