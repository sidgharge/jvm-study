package come.homeproects.jvmstudy.parser.statements;

import come.homeproects.jvmstudy.parser.expressions.SyntaxExpression;
import come.homeproects.jvmstudy.parser.lexer.Token;

public record ExpressionSyntaxStatement(SyntaxExpression expression, Token semiColonToken) implements SyntaxStatement {

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
        return "  ".repeat(indent) + expression.toString() + semiColonToken.value();
    }
}