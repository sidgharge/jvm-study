package come.homeproects.jvmstudy.parser.expressions;

import come.homeproects.jvmstudy.parser.lexer.Token;

import java.util.Collections;
import java.util.List;

public record NumberExpression(Token token) implements Expression {

    @Override
    public List<Expression> children() {
        return Collections.emptyList();
    }

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.NUMBER;
    }

    @Override
    public String toString() {
        return token.value();
    }
}
