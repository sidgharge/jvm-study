package come.homeproects.jvmstudy.parser.evaluator;

import come.homeproects.jvmstudy.parser.expressions.BinarySyntaxExpression;
import come.homeproects.jvmstudy.parser.expressions.SyntaxExpression;
import come.homeproects.jvmstudy.parser.expressions.LiteralSyntaxExpression;
import come.homeproects.jvmstudy.parser.expressions.UnarySyntaxExpression;

public class Evaluator2 {

    public Object evaluate(SyntaxExpression syntaxExpression) {
        return switch (syntaxExpression) {
            case BinarySyntaxExpression binaryExpression -> binaryExpression(binaryExpression);
            case UnarySyntaxExpression unaryExpression -> unaryExpression(unaryExpression);
            case LiteralSyntaxExpression literalExpression -> literalExpression(literalExpression);
            default -> throw new RuntimeException("Unhandled expression type " + syntaxExpression.expressionType());
        };
    }

    private Object literalExpression(LiteralSyntaxExpression literalExpression) {
        return switch (literalExpression.token().type()) {
            case KEYWORD_TRUE_TOKEN -> true;
            case KEYWORD_FALSE_TOKEN -> false;
            case NUMBER_TOKEN -> Integer.parseInt(literalExpression.token().value());
            default -> throw new RuntimeException("Unhandled keyword " + literalExpression.token().value());
        };
    }

    private Object unaryExpression(UnarySyntaxExpression unaryExpression) {
        return switch (unaryExpression.operator().type()) {
            case PLUS_TOKEN -> (int)evaluate(unaryExpression.syntaxExpression());
            case MINUS_TOKEN -> -(int)evaluate(unaryExpression.syntaxExpression());
            case BANG_TOKEN -> !(boolean)evaluate(unaryExpression.syntaxExpression());
            default -> throw new RuntimeException("Invalid unary operator at index: " + unaryExpression.operator().startIndex());
        };
    }

    private Object binaryExpression(BinarySyntaxExpression binaryExpression) {
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
