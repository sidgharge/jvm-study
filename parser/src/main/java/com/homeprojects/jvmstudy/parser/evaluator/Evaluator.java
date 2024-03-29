package com.homeprojects.jvmstudy.parser.evaluator;

import com.homeprojects.jvmstudy.parser.binder.expressions.ArgumentBoundExpression;
import com.homeprojects.jvmstudy.parser.binder.expressions.BinaryBoundExpression;
import com.homeprojects.jvmstudy.parser.binder.expressions.BoundExpression;
import com.homeprojects.jvmstudy.parser.binder.expressions.LiteralBoundExpression;
import com.homeprojects.jvmstudy.parser.binder.expressions.MethodCallBoundExpression;
import com.homeprojects.jvmstudy.parser.binder.expressions.UnaryBoundExpression;
import com.homeprojects.jvmstudy.parser.binder.statements.BlockBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.BoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.ExpressionBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.MethodDeclarationBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.ParameterBound;
import com.homeprojects.jvmstudy.parser.binder.statements.ReturnBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.VariableDeclarationBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.VariableReassignmentBoundStatement;
import com.homeprojects.jvmstudy.parser.lowerer.ConditionalGotoBoundStatement;
import com.homeprojects.jvmstudy.parser.lowerer.GotoBoundStatement;
import com.homeprojects.jvmstudy.parser.lowerer.Label;
import com.homeprojects.jvmstudy.parser.lowerer.LabelBoundStatement;
import com.homeprojects.jvmstudy.parser.types.Type;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Evaluator {

    private final BoundStatement parent;

    private final List<Map<String, Object>> variables;

    private final List<Map<Label, Integer>> labels;

    private final Map<String, Object> methods;

    private boolean isMethodReturned = false;

    private Object lastValue;

    public Evaluator(BoundStatement parent) {
        this.parent = parent;
        this.variables = new ArrayList<>();
        this.labels = new ArrayList<>();
        this.methods = new HashMap<>();
        addGlobalMethods();
    }

    public Object evaluate() {
        Object result = boundStatement(parent);
        lastValue = null;
        return result;
    }

    public Object evaluate(BoundStatement child) {
        if (parent instanceof BlockBoundStatement blockBoundStatement) {
            blockBoundStatement.statements().add(child);
        }
        Object result = boundStatement(child);
        lastValue = null;
        return result;
    }

    private Object boundStatement(BoundStatement statement) {
        if (isMethodReturned) {
            return lastValue;
        }
        switch (statement) {
            case ExpressionBoundStatement expressionBoundStatement -> expressionBoundStatement(expressionBoundStatement);
            case BlockBoundStatement blockBoundStatement -> blockBoundStatement(blockBoundStatement);
            case VariableDeclarationBoundStatement variableDeclarationBoundStatement -> variableDeclarationBoundStatement(variableDeclarationBoundStatement);
            case VariableReassignmentBoundStatement variableReassignmentBoundStatement -> variableReassignmentBoundStatement(variableReassignmentBoundStatement);
            case LabelBoundStatement __ -> {}
            case MethodDeclarationBoundStatement methodDeclarationBoundStatement -> methodDeclarationBoundStatement(methodDeclarationBoundStatement);
            case ReturnBoundStatement returnBoundStatement -> returnBoundStatement(returnBoundStatement);
            default -> throw new RuntimeException("Unhandled statement type: " + statement.getClass());
        }
        return lastValue;
    }

    private void returnBoundStatement(ReturnBoundStatement returnBoundStatement) {
        isMethodReturned = true;
        lastValue = boundExpression(returnBoundStatement.expression());
    }

    private void methodDeclarationBoundStatement(MethodDeclarationBoundStatement methodDeclarationBoundStatement) {
        String methodName = methodDeclarationBoundStatement.methodNameToken().value();
        List<ParameterBound> parameters = methodDeclarationBoundStatement.parametersBound().parameters();
        Function<List<Object>, Object> fn = list -> {
            variables.add(new HashMap<>());
            for (int i = 0; i < parameters.size(); i++) {
                ParameterBound parameter = parameters.get(i);
                variables.getLast().put(parameter.parameterNameToken().value(), list.get(i));
            }
            blockBoundStatement(methodDeclarationBoundStatement.methodBody());
            variables.removeLast();
            return lastValue;
        };
        methods.put(methodName, fn);
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

    private Object boundExpression(BoundExpression expression) {
        return switch (expression) {
            case BinaryBoundExpression binaryExpression -> binaryExpression(binaryExpression);
            case UnaryBoundExpression unaryExpression -> unaryExpression(unaryExpression);
            case LiteralBoundExpression literalExpression -> literalExpression(literalExpression);
            case MethodCallBoundExpression methodCallBoundExpression -> methodCallBoundExpression(methodCallBoundExpression);
            default -> throw new RuntimeException("Unhandled expression type " + expression.getClass());
        };
    }

    private Object methodCallBoundExpression(MethodCallBoundExpression methodCallBoundExpression) {
        List<ArgumentBoundExpression> expressions = methodCallBoundExpression.arguments().expressions();
        Object[] args = new Object[expressions.size()];
        for (int i = 0; i < args.length; i++) {
            Object arg = argumentBoundExpression(expressions.get(i));
            args[i] = arg;
        }
        isMethodReturned = false;
        invokeMethod(methodCallBoundExpression.methodName().value(), args);
        isMethodReturned = false;
        return lastValue;
    }

    private Object argumentBoundExpression(ArgumentBoundExpression argumentBoundExpression) {
        return boundExpression(argumentBoundExpression.expression());
    }

    private Object literalExpression(LiteralBoundExpression literalExpression) {
        return switch (literalExpression.token().type()) {
            case KEYWORD_TRUE_TOKEN -> true;
            case KEYWORD_FALSE_TOKEN -> false;
            case NUMBER_TOKEN -> Integer.parseInt(literalExpression.token().value());
            case IDENTIFIER_TOKEN -> findVariableValue(literalExpression.token().value()).orElse(null);
            case STRING_TOKEN -> literalExpression.token().value();
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
            case PLUS_TOKEN -> handlePlusToken(binaryExpression);
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

    private Object handlePlusToken(BinaryBoundExpression binaryExpression) {
        if (binaryExpression.type() == Type.INT) {
            return (int) boundExpression(binaryExpression.left()) + (int) boundExpression(binaryExpression.right());
        } else {
            return boundExpression(binaryExpression.left()).toString() + boundExpression(binaryExpression.right()).toString();
        }
    }

    private void addGlobalMethods() {
        Function<List<Object>, Object> println = (objects) -> {
            String line = objects.stream().map(o -> o.toString()).collect(Collectors.joining(" "));
            System.out.println(line);
            return null;
        };
        this.methods.put("println", println);

        Function<List<Object>, Object> input = (__) -> {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
//            scanner.close();
            return line;
        };
        this.methods.put("input", input);
    }

    private void invokeMethod(String name, Object... params) {
        Object o = methods.get(name);
        try {
            lastValue = o.getClass().getDeclaredMethod("apply", Object.class).invoke(o, Arrays.asList(params));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
