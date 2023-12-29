package come.homeproects.jvmstudy.parser.binder.expressions;

import come.homeproects.jvmstudy.parser.lexer.Token;
import come.homeproects.jvmstudy.parser.types.Type;

public record LiteralBoundExpression(Token token, Type type) implements BoundExpression {

    @Override
    public String toString() {
        return token.value();
    }
}
