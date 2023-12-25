package come.homeproects.jvmstudy.parser.bindexpressions;

import come.homeproects.jvmstudy.parser.lexer.Token;

import java.lang.reflect.Type;

public record BinaryBoundExpression(BoundExpression left, BoundExpression right, Token operatorToken, Type type) implements BoundExpression {
}
