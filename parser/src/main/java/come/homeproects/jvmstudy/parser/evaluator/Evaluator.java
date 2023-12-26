package come.homeproects.jvmstudy.parser.evaluator;

import come.homeproects.jvmstudy.parser.binder.expressions.BinaryBoundExpression;
import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression;
import come.homeproects.jvmstudy.parser.binder.expressions.LiteralBoundExpression;
import come.homeproects.jvmstudy.parser.binder.expressions.UnaryBoundExpression;
import come.homeproects.jvmstudy.parser.binder.statements.BlockBoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.BoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.ExpressionBoundStatement;
import come.homeproects.jvmstudy.parser.lexer.TokenType;

import java.util.HashMap;
import java.util.Map;

public class Evaluator {

    Map<String, Object> variables = new HashMap<>();

    private Object lastValue;

    public Object evaluate(BoundStatement statement) {
        switch (statement) {
            case ExpressionBoundStatement expressionBoundStatement -> expressionBoundStatement(expressionBoundStatement);
            case BlockBoundStatement blockBoundStatement -> blockBoundStatement(blockBoundStatement);
            default -> throw new RuntimeException("Unhandled statement type: " + statement.getClass());
        }
        return lastValue;
    }

    private void blockBoundStatement(BlockBoundStatement blockBoundStatement) {
        for (BoundStatement statement : blockBoundStatement.statements()) {
            evaluate(statement);
        }
    }

    private void expressionBoundStatement(ExpressionBoundStatement expressionBoundStatement) {
        lastValue = evaluate(expressionBoundStatement.expression());
    }

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
            case IDENTIFIER_TOKEN -> variables.getOrDefault(literalExpression.token().value(), null);
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
            case EQUALS_TOKEN -> assignmentEvaluation(binaryExpression);
            default -> throw new RuntimeException("Unknown token: " + binaryExpression.operatorToken());
        };
    }

    private Object assignmentEvaluation(BinaryBoundExpression binaryExpression) {
        if (!(binaryExpression.left() instanceof LiteralBoundExpression) || !((LiteralBoundExpression) binaryExpression.left()).token().type().equals(TokenType.IDENTIFIER_TOKEN)) {
            throw new RuntimeException("Left side of assignment is not a variable");
        }
        Object result = evaluate(binaryExpression.right());
        String variableName = ((LiteralBoundExpression) binaryExpression.left()).token().value();
        variables.put(variableName, result);
        return result;
    }
}
