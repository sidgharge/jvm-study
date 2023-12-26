package come.homeproects.jvmstudy.parser.expressions;

import come.homeproects.jvmstudy.parser.lexer.Token;

public record BinarySyntaxExpression(SyntaxExpression left, SyntaxExpression right, Token token) implements SyntaxExpression {

    @Override
    public SyntaxExpressionType expressionType() {
        return SyntaxExpressionType.BINARY_EXPRESSION;
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left.toString(), token.value(), right.toString());
    }
}
