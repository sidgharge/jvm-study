package come.homeproects.jvmstudy.parser.binder.expressions;

import come.homeproects.jvmstudy.parser.lexer.Token;
import come.homeproects.jvmstudy.parser.types.Type;

public record BinaryBoundExpression(BoundExpression left, BoundExpression right, Token operatorToken, Type type) implements BoundExpression {

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left.toString(), operatorToken.value(), right.toString());
    }
}
