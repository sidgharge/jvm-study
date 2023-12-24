package come.homeproects.jvmstudy.parser.expressions;

import come.homeproects.jvmstudy.parser.lexer.Token;

public record LiteralExpression(Token token) implements Expression {

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.LITERAL;
    }

    @Override
    public String toString() {
        return token.value();
    }
}
