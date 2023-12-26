package come.homeproects.jvmstudy.parser.expressions;

import come.homeproects.jvmstudy.parser.lexer.Token;

public record LiteralSyntaxExpression(Token token) implements SyntaxExpression {

    @Override
    public SyntaxExpressionType expressionType() {
        return SyntaxExpressionType.LITERAL;
    }

    @Override
    public String toString() {
        return token.value();
    }
}
