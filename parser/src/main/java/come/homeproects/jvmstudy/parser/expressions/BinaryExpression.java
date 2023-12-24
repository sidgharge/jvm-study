package come.homeproects.jvmstudy.parser.expressions;

import come.homeproects.jvmstudy.parser.lexer.Token;

public record BinaryExpression(Expression left, Expression right, Token token) implements Expression {

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.BINARY_EXPRESSION;
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left.toString(), token.value(), right.toString());
    }
}
