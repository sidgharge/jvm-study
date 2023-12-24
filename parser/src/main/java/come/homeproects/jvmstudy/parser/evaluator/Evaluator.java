package come.homeproects.jvmstudy.parser.evaluator;

import come.homeproects.jvmstudy.parser.expressions.BinaryExpression;
import come.homeproects.jvmstudy.parser.expressions.Expression;
import come.homeproects.jvmstudy.parser.expressions.NumberExpression;

public class Evaluator {

    public int evaluate(Expression expression) {
        return switch (expression) {
            case NumberExpression numberExpression -> Integer.parseInt(numberExpression.token().value());
            case BinaryExpression binaryExpression -> binaryExpression(binaryExpression);
            default -> throw new RuntimeException("Unhandled expression type " + expression.expressionType());
        };
    }

    private int binaryExpression(BinaryExpression binaryExpression) {
        return switch (binaryExpression.token().type()) {
            case PLUS_TOKEN -> evaluate(binaryExpression.left()) + evaluate(binaryExpression.right());
            case MINUS_TOKEN -> evaluate(binaryExpression.left()) - evaluate(binaryExpression.right());
            case MULTIPLICATION_TOKEN -> evaluate(binaryExpression.left()) * evaluate(binaryExpression.right());
            case DIVISION_TOKEN -> evaluate(binaryExpression.left()) / evaluate(binaryExpression.right());
            default -> throw new RuntimeException("Unknown token");
        };
    }
}
