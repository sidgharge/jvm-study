package come.homeproects.jvmstudy.parser.expressions;

import come.homeproects.jvmstudy.parser.lexer.Token;

public record NumberExpression(Token token) implements Expression {

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.NUMBER;
    }

    @Override
    public String toString() {
        return token.value();
    }
}
