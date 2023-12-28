package come.homeproects.jvmstudy.parser.evaluator;

import come.homeproects.jvmstudy.parser.binder.expressions.BinaryBoundExpression;
import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression;
import come.homeproects.jvmstudy.parser.binder.expressions.LiteralBoundExpression;
import come.homeproects.jvmstudy.parser.binder.expressions.UnaryBoundExpression;
import come.homeproects.jvmstudy.parser.binder.statements.BlockBoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.BoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.ElseBlockBoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.ExpressionBoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.IfBlockBoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.VariableDeclarationBoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.VariableReassignmentBoundStatement;
import come.homeproects.jvmstudy.parser.lexer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Evaluator {

    private final List<Map<String, Object>> variables;

    private Object lastValue;

    public Evaluator() {
        this.variables = new ArrayList<>();
    }

    public Object evaluate(BoundStatement statement) {
        switch (statement) {
            case ExpressionBoundStatement expressionBoundStatement -> expressionBoundStatement(expressionBoundStatement);
            case BlockBoundStatement blockBoundStatement -> blockBoundStatement(blockBoundStatement);
            case VariableDeclarationBoundStatement variableDeclarationBoundStatement -> variableDeclarationBoundStatement(variableDeclarationBoundStatement);
            case VariableReassignmentBoundStatement variableReassignmentBoundStatement -> variableReassignmentBoundStatement(variableReassignmentBoundStatement);
            case IfBlockBoundStatement ifBlockBoundStatement-> ifBlockBoundStatement(ifBlockBoundStatement);
            default -> throw new RuntimeException("Unhandled statement type: " + statement.getClass());
        }
        return lastValue;
    }

    private void ifBlockBoundStatement(IfBlockBoundStatement ifBlockBoundStatement) {
        boolean condition = (boolean) evaluate(ifBlockBoundStatement.condition());
        if (condition) {
            evaluate(ifBlockBoundStatement.ifBlockBody());
        } else {
            ifBlockBoundStatement.elseBlockBody().map(ElseBlockBoundStatement::elseBlockBody).ifPresent(this::evaluate);
        }
    }

    private void variableReassignmentBoundStatement(VariableReassignmentBoundStatement variableReassignmentBoundStatement) {
        BoundExpression expression = variableReassignmentBoundStatement.expression();
        Object result = evaluate(expression);
        String name = variableReassignmentBoundStatement.identifierToken().value();
        findVariableMap(name).map(m -> m.put(name, result));
        lastValue = result;
    }

    private void variableDeclarationBoundStatement(VariableDeclarationBoundStatement variableDeclarationBoundStatement) {
        BoundExpression expression = variableDeclarationBoundStatement.expression();
        Object result = evaluate(expression);
        variables.getLast().put(variableDeclarationBoundStatement.identifierToken().value(), result);
        lastValue = result;
    }

    private void blockBoundStatement(BlockBoundStatement blockBoundStatement) {
        variables.add(new HashMap<>());
        for (BoundStatement statement : blockBoundStatement.statements()) {
            evaluate(statement);
        }
        variables.removeLast();
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
            case IDENTIFIER_TOKEN -> findVariableValue(literalExpression.token().value()).orElse(null);
            default -> throw new RuntimeException("Unhandled keyword " + literalExpression.token().value());
        };
    }

    private Optional<Object> findVariableValue(String name) {
        return findVariableMap(name).map(m -> m.get(name));
    }

    private Optional<Map<String, Object>> findVariableMap(String name) {
        for (int i = variables.size() - 1; i >= 0; i--) {
            if (variables.get(i).containsKey(name)) {
                return Optional.ofNullable(variables.get(i));
            }
        }
        return Optional.empty();
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
            case GREATER_THAN_TOKEN -> (int)evaluate(binaryExpression.left()) > (int)evaluate(binaryExpression.right());
            case GREATER_THAN_EQUALS_TOKEN -> (int)evaluate(binaryExpression.left()) >= (int)evaluate(binaryExpression.right());
            case LESS_THAN_TOKEN -> (int)evaluate(binaryExpression.left()) < (int)evaluate(binaryExpression.right());
            case LESS_THAN_EQUALS_TOKEN -> (int)evaluate(binaryExpression.left()) <= (int)evaluate(binaryExpression.right());
            case EQUALS_TOKEN -> assignmentEvaluation(binaryExpression);
            default -> throw new RuntimeException("Unknown token: " + binaryExpression.operatorToken());
        };
    }

    private Object assignmentEvaluation(BinaryBoundExpression binaryExpression) {
        if (true) {
            throw new RuntimeException("COde should never come here");
        }
        if (!(binaryExpression.left() instanceof LiteralBoundExpression) || !((LiteralBoundExpression) binaryExpression.left()).token().type().equals(TokenType.IDENTIFIER_TOKEN)) {
            throw new RuntimeException("Left side of assignment is not a variable");
        }
        Object result = evaluate(binaryExpression.right());
        String variableName = ((LiteralBoundExpression) binaryExpression.left()).token().value();
//        variables.put(variableName, result);
        return result;
    }
}
