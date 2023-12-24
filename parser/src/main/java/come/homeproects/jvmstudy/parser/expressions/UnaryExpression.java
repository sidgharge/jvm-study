package come.homeproects.jvmstudy.parser.expressions;

import come.homeproects.jvmstudy.parser.lexer.Token;

public record UnaryExpression(Token operator, Expression expression) implements Expression {
    @Override
    public ExpressionType expressionType() {
        return ExpressionType.UNARY;
    }

    @Override
    public String toString() {
        return operator.value() + expression;
    }
}
