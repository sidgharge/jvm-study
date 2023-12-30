package com.homeprojects.jvmstudy.parser.evaluator;

import com.homeprojects.jvmstudy.parser.binder.statements.BlockBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.BoundStatement;
import com.homeprojects.jvmstudy.parser.binder.expressions.BinaryBoundExpression;
import com.homeprojects.jvmstudy.parser.binder.expressions.BoundExpression;
import com.homeprojects.jvmstudy.parser.binder.expressions.LiteralBoundExpression;
import com.homeprojects.jvmstudy.parser.binder.expressions.UnaryBoundExpression;
import com.homeprojects.jvmstudy.parser.binder.statements.ExpressionBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.VariableDeclarationBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.VariableReassignmentBoundStatement;
import com.homeprojects.jvmstudy.parser.lowerer.ConditionalGotoBoundStatement;
import com.homeprojects.jvmstudy.parser.lowerer.GotoBoundStatement;
import com.homeprojects.jvmstudy.parser.lowerer.LabelBoundStatement;
import com.homeprojects.jvmstudy.parser.lowerer.Label;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Evaluator {

    private final List<Map<String, Object>> variables;

    private final List<Map<Label, Integer>> labels;

    private Object lastValue;

    public Evaluator() {
        this.variables = new ArrayList<>();
        this.labels = new ArrayList<>();
    }
    
    public Object evaluate(BoundStatement statement) {
        Object result = boundStatement(statement);
        lastValue = null;
        return result;
    } 
    
    public Object boundStatement(BoundStatement statement) {
        switch (statement) {
            case ExpressionBoundStatement expressionBoundStatement -> expressionBoundStatement(expressionBoundStatement);
            case BlockBoundStatement blockBoundStatement -> blockBoundStatement(blockBoundStatement);
            case VariableDeclarationBoundStatement variableDeclarationBoundStatement -> variableDeclarationBoundStatement(variableDeclarationBoundStatement);
            case VariableReassignmentBoundStatement variableReassignmentBoundStatement -> variableReassignmentBoundStatement(variableReassignmentBoundStatement);
            case LabelBoundStatement __ -> {}
            default -> throw new RuntimeException("Unhandled statement type: " + statement.getClass());
        }
        return lastValue;
    }

    private void variableReassignmentBoundStatement(VariableReassignmentBoundStatement variableReassignmentBoundStatement) {
        BoundExpression expression = variableReassignmentBoundStatement.expression();
        Object result = boundExpression(expression);
        String name = variableReassignmentBoundStatement.identifierToken().value();
        findVariableMap(name).map(m -> m.put(name, result));
        lastValue = result;
    }

    private void variableDeclarationBoundStatement(VariableDeclarationBoundStatement variableDeclarationBoundStatement) {
        BoundExpression expression = variableDeclarationBoundStatement.expression();
        Object result = boundExpression(expression);
        variables.getLast().put(variableDeclarationBoundStatement.identifierToken().value(), result);
        lastValue = result;
    }

    private void blockBoundStatement(BlockBoundStatement blockBoundStatement) {
        variables.add(new HashMap<>());
        labels.add(new HashMap<>());

        for (int i = 0; i < blockBoundStatement.statements().size(); i++) {
            BoundStatement statement = blockBoundStatement.statements().get(i);
            if (statement instanceof LabelBoundStatement labelBoundStatement) {
                labels.getLast().put(labelBoundStatement.label(), i);
            }
        }

        for (int i = 0; i < blockBoundStatement.statements().size(); i++) {
            BoundStatement statement = blockBoundStatement.statements().get(i);
            if (statement instanceof ConditionalGotoBoundStatement conditionalGotoBoundStatement) {
                int jumpLine = conditionalGotoBoundStatement(conditionalGotoBoundStatement);
                i = Math.max(i, jumpLine);
                continue;
            }
            if (statement instanceof GotoBoundStatement gotoBoundStatement) {
                i = labels.getLast().get(gotoBoundStatement.label());
                continue;
            }

            boundStatement(statement);
        }
        variables.removeLast();
        labels.removeLast();
    }

    private int conditionalGotoBoundStatement(ConditionalGotoBoundStatement conditionalGotoBoundStatement) {
        boolean result = (boolean) boundExpression(conditionalGotoBoundStatement.condition());
        if (result) {
            return -1;
        }
        return labels.getLast().get(conditionalGotoBoundStatement.otherwiseLabel());
    }

    private void expressionBoundStatement(ExpressionBoundStatement expressionBoundStatement) {
        lastValue = boundExpression(expressionBoundStatement.expression());
    }

    public Object boundExpression(BoundExpression expression) {
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
            case PLUS_TOKEN -> (int) boundExpression(unaryExpression.operand());
            case MINUS_TOKEN -> -(int) boundExpression(unaryExpression.operand());
            case BANG_TOKEN -> !(boolean) boundExpression(unaryExpression.operand());
            default -> throw new RuntimeException("Invalid unary operator at index: " + unaryExpression.operatorToken().startIndex());
        };
    }

    private Object binaryExpression(BinaryBoundExpression binaryExpression) {
        return switch (binaryExpression.operatorToken().type()) {
            case PLUS_TOKEN -> (int) boundExpression(binaryExpression.left()) + (int) boundExpression(binaryExpression.right());
            case MINUS_TOKEN -> (int) boundExpression(binaryExpression.left()) - (int) boundExpression(binaryExpression.right());
            case START_TOKEN -> (int) boundExpression(binaryExpression.left()) * (int) boundExpression(binaryExpression.right());
            case SLASH_TOKEN -> (int) boundExpression(binaryExpression.left()) / (int) boundExpression(binaryExpression.right());
            case DOUBLE_AMPERSAND_TOKEN -> (boolean) boundExpression(binaryExpression.left()) && (boolean) boundExpression(binaryExpression.right());
            case DOUBLE_PIPE_TOKEN -> (boolean) boundExpression(binaryExpression.left()) || (boolean) boundExpression(binaryExpression.right());
            case DOUBLE_EQUALS_TOKEN -> boundExpression(binaryExpression.left()).equals(boundExpression(binaryExpression.right()));
            case BANG_EQUALS_TOKEN -> !boundExpression(binaryExpression.left()).equals(boundExpression(binaryExpression.right()));
            case GREATER_THAN_TOKEN -> (int) boundExpression(binaryExpression.left()) > (int) boundExpression(binaryExpression.right());
            case GREATER_THAN_EQUALS_TOKEN -> (int) boundExpression(binaryExpression.left()) >= (int) boundExpression(binaryExpression.right());
            case LESS_THAN_TOKEN -> (int) boundExpression(binaryExpression.left()) < (int) boundExpression(binaryExpression.right());
            case LESS_THAN_EQUALS_TOKEN -> (int) boundExpression(binaryExpression.left()) <= (int) boundExpression(binaryExpression.right());
            default -> throw new RuntimeException("Unknown token: " + binaryExpression.operatorToken());
        };
    }
}
