package come.homeproects.jvmstudy.parser.binder.expressions;

import come.homeproects.jvmstudy.parser.lexer.Token;
import come.homeproects.jvmstudy.parser.types.Type;

public record UnaryBoundExpression(Token operatorToken, Type type, BoundExpression operand) implements BoundExpression {

    @Override
    public String toString() {
        return String.format("%s%s", operatorToken.value(), operand.toString());
    }
}
