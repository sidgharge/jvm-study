package come.homeproects.jvmstudy.parser.evaluator;

import come.homeproects.jvmstudy.parser.expressions.BinaryExpression;
import come.homeproects.jvmstudy.parser.expressions.Expression;
import come.homeproects.jvmstudy.parser.expressions.LiteralExpression;
import come.homeproects.jvmstudy.parser.expressions.UnaryExpression;

public class Evaluator {

    public Object evaluate(Expression expression) {
        return switch (expression) {
            case BinaryExpression binaryExpression -> binaryExpression(binaryExpression);
            case UnaryExpression unaryExpression -> unaryExpression(unaryExpression);
            case LiteralExpression literalExpression -> literalExpression(literalExpression);
            default -> throw new RuntimeException("Unhandled expression type " + expression.expressionType());
        };
    }

    private Object literalExpression(LiteralExpression literalExpression) {
        return switch (literalExpression.token().type()) {
            case KEYWORD_TRUE_TOKEN -> true;
            case KEYWORD_FALSE_TOKEN -> false;
            case NUMBER_TOKEN -> Integer.parseInt(literalExpression.token().value());
            default -> throw new RuntimeException("Unhandled keyword " + literalExpression.token().value());
        };
    }

    private Object unaryExpression(UnaryExpression unaryExpression) {
        return switch (unaryExpression.operator().type()) {
            case PLUS_TOKEN -> (int)evaluate(unaryExpression.expression());
            case MINUS_TOKEN -> -(int)evaluate(unaryExpression.expression());
            case BANG_TOKEN -> !(boolean)evaluate(unaryExpression.expression());
            default -> throw new RuntimeException("Invalid unary operator at index: " + unaryExpression.operator().startIndex());
        };
    }

    private Object binaryExpression(BinaryExpression binaryExpression) {
        return switch (binaryExpression.token().type()) {
            case PLUS_TOKEN -> (int)evaluate(binaryExpression.left()) + (int)evaluate(binaryExpression.right());
            case MINUS_TOKEN -> (int)evaluate(binaryExpression.left()) - (int)evaluate(binaryExpression.right());
            case START_TOKEN -> (int)evaluate(binaryExpression.left()) * (int)evaluate(binaryExpression.right());
            case SLASH_TOKEN -> (int)evaluate(binaryExpression.left()) / (int)evaluate(binaryExpression.right());
            case DOUBLE_AMPERSAND_TOKEN -> (boolean)evaluate(binaryExpression.left()) && (boolean)evaluate(binaryExpression.right());
            case DOUBLE_PIPE_TOKEN -> (boolean)evaluate(binaryExpression.left()) || (boolean)evaluate(binaryExpression.right());
            case DOUBLE_EQUALS_TOKEN -> evaluate(binaryExpression.left()).equals(evaluate(binaryExpression.right()));
            case BANG_EQUALS_TOKEN -> !evaluate(binaryExpression.left()).equals(evaluate(binaryExpression.right()));
            default -> throw new RuntimeException("Unknown token");
        };
    }
}
