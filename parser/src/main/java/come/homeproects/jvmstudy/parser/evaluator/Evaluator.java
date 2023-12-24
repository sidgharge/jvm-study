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
            case KEYWORD_TRUE -> true;
            case KEYWORD_FALSE -> false;
            case NUMBER_TOKEN -> Integer.parseInt(literalExpression.token().value());
            default -> throw new RuntimeException("Unhandled keyword " + literalExpression.token().value());
        };
    }

    private Object unaryExpression(UnaryExpression unaryExpression) {
        return switch (unaryExpression.operator().type()) {
            case PLUS_TOKEN -> (int)evaluate(unaryExpression.expression());
            case MINUS_TOKEN -> -(int)evaluate(unaryExpression.expression());
            default -> throw new RuntimeException("Invalid unary operator at index: " + unaryExpression.operator().startIndex());
        };
    }

    private Object binaryExpression(BinaryExpression binaryExpression) {
        return switch (binaryExpression.token().type()) {
            case PLUS_TOKEN -> (int)evaluate(binaryExpression.left()) + (int)evaluate(binaryExpression.right());
            case MINUS_TOKEN -> (int)evaluate(binaryExpression.left()) - (int)evaluate(binaryExpression.right());
            case MULTIPLICATION_TOKEN -> (int)evaluate(binaryExpression.left()) * (int)evaluate(binaryExpression.right());
            case DIVISION_TOKEN -> (int)evaluate(binaryExpression.left()) / (int)evaluate(binaryExpression.right());
            case KEYWORD_AND -> (boolean)evaluate(binaryExpression.left()) && (boolean)evaluate(binaryExpression.right());
            case KEYWORD_OR -> (boolean)evaluate(binaryExpression.left()) || (boolean)evaluate(binaryExpression.right());
            default -> throw new RuntimeException("Unknown token");
        };
    }
}
