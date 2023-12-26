package come.homeproects.jvmstudy.parser.expressions;

import come.homeproects.jvmstudy.parser.lexer.Token;

public record UnarySyntaxExpression(Token operator, SyntaxExpression syntaxExpression) implements SyntaxExpression {
    @Override
    public SyntaxExpressionType expressionType() {
        return SyntaxExpressionType.UNARY;
    }

    @Override
    public String toString() {
        return operator.value() + syntaxExpression;
    }
}
