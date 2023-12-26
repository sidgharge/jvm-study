package come.homeproects.jvmstudy.parser.evaluator;

import come.homeproects.jvmstudy.parser.bindexpressions.BinaryBoundExpression;
import come.homeproects.jvmstudy.parser.bindexpressions.BoundExpression;
import come.homeproects.jvmstudy.parser.bindexpressions.LiteralBoundExpression;
import come.homeproects.jvmstudy.parser.bindexpressions.UnaryBoundExpression;

public class Evaluator {

    public Object evaluate(BoundExpression expression) {
        return switch (expression) {
            case BinaryBoundExpression binaryExpression -> binaryExpression(binaryExpression);
            case UnaryBoundExpression unaryExpression -> unaryExpression(unaryExpression);
            case LiteralBoundExpression literalExpression -> literalExpression(literalExpression);
            default -> throw new RuntimeException("Unhandled expression type " + expression.getClass());
        };
    }

    private Object literalExpression(LiteralBoundExpression literalExpression) {
        return switch (literalExpression.token().type()) {
            case KEYWORD_TRUE_TOKEN -> true;
            case KEYWORD_FALSE_TOKEN -> false;
            case NUMBER_TOKEN -> Integer.parseInt(literalExpression.token().value());
            default -> throw new RuntimeException("Unhandled keyword " + literalExpression.token().value());
        };
    }

    private Object unaryExpression(UnaryBoundExpression unaryExpression) {
        return switch (unaryExpression.operatorToken().type()) {
            case PLUS_TOKEN -> (int)evaluate(unaryExpression.operand());
            case MINUS_TOKEN -> -(int)evaluate(unaryExpression.operand());
            case BANG_TOKEN -> !(boolean)evaluate(unaryExpression.operand());
            default -> throw new RuntimeException("Invalid unary operator at index: " + unaryExpression.operatorToken().startIndex());
        };
    }

    private Object binaryExpression(BinaryBoundExpression binaryExpression) {
        return switch (binaryExpression.operatorToken().type()) {
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
