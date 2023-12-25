package come.homeproects.jvmstudy.parser.bindexpressions;

import come.homeproects.jvmstudy.parser.lexer.Token;

import java.lang.reflect.Type;

public record LiteralBoundExpression(Token token, Type type) implements BoundExpression {
}
