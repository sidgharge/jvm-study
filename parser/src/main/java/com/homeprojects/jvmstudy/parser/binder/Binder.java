package com.homeprojects.jvmstudy.parser.binder;

import com.homeprojects.jvmstudy.parser.binder.expressions.ArgumentBoundExpression;
import com.homeprojects.jvmstudy.parser.binder.expressions.ArgumentsBound;
import com.homeprojects.jvmstudy.parser.binder.expressions.BinaryBoundExpression;
import com.homeprojects.jvmstudy.parser.binder.expressions.BoundExpression;
import com.homeprojects.jvmstudy.parser.binder.expressions.LiteralBoundExpression;
import com.homeprojects.jvmstudy.parser.binder.expressions.MethodCallBoundExpression;
import com.homeprojects.jvmstudy.parser.binder.expressions.UnaryBoundExpression;
import com.homeprojects.jvmstudy.parser.binder.statements.BlockBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.BoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.ElseBlockBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.IfBlockBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.MethodDeclarationBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.ParameterBound;
import com.homeprojects.jvmstudy.parser.binder.statements.ParametersBound;
import com.homeprojects.jvmstudy.parser.binder.statements.ReturnBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.VariableReassignmentBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.WhileBlockBoundStatement;
import com.homeprojects.jvmstudy.parser.diagnostics.Diagnostics;
import com.homeprojects.jvmstudy.parser.expressions.ArgumentSyntax;
import com.homeprojects.jvmstudy.parser.expressions.ArgumentsSyntax;
import com.homeprojects.jvmstudy.parser.expressions.SyntaxExpression;
import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.statements.BlockSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.ElseBlockSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.ExpressionSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.IfBlockSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.MethodCallSyntaxExpression;
import com.homeprojects.jvmstudy.parser.statements.MethodDeclarationSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.ParameterSyntax;
import com.homeprojects.jvmstudy.parser.statements.ParametersSyntax;
import com.homeprojects.jvmstudy.parser.statements.ReturnSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.SyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.VariableReassignmentSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.WhileBlockSyntaxStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.ExpressionBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.ForBlockBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.VariableDeclarationBoundStatement;
import com.homeprojects.jvmstudy.parser.expressions.BinarySyntaxExpression;
import com.homeprojects.jvmstudy.parser.expressions.LiteralSyntaxExpression;
import com.homeprojects.jvmstudy.parser.expressions.UnarySyntaxExpression;
import com.homeprojects.jvmstudy.parser.lexer.TokenType;
import com.homeprojects.jvmstudy.parser.statements.ForBlockSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.VariableDeclarationSyntaxStatement;
import com.homeprojects.jvmstudy.parser.types.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Binder {

    private final Diagnostics diagnostics;

    private final BoundedScope scope;

    private final BlockSyntaxStatement parent;

    public Binder(BlockSyntaxStatement syntaxStatement) {
        this.parent = syntaxStatement;
        this.diagnostics = new Diagnostics();
        this.scope = new BoundedScope();
        this.scope.newScope();
        addGlobalMethods();
    }

    public BoundStatement bind() {
        List<BoundStatement> topLevelStatements = parent.statements().stream()
                .sorted(Comparator.comparing(s -> s instanceof MethodDeclarationSyntaxStatement ? 1 : 2))
                .map(this::topLevelStatements)
                .toList();

        return new BlockBoundStatement(parent.openBracket(), parent.closedBracket(), topLevelStatements);
    }

    private BoundStatement topLevelStatements(SyntaxStatement statement) {
        return switch (statement) {
            case BlockSyntaxStatement blockSyntaxStatement -> blockSyntaxStatement(blockSyntaxStatement);
            case ExpressionSyntaxStatement expressionSyntaxStatement -> expressionSyntaxStatement(expressionSyntaxStatement);
            case VariableDeclarationSyntaxStatement variableDeclarationSyntaxStatement -> variableDeclarationSyntaxStatement(variableDeclarationSyntaxStatement);
            case VariableReassignmentSyntaxStatement variableReassignmentSyntaxStatement -> variableReassignmentSyntaxStatement(variableReassignmentSyntaxStatement);
            case IfBlockSyntaxStatement ifBlockSyntaxStatement -> ifBlockSyntaxStatement(ifBlockSyntaxStatement);
            case WhileBlockSyntaxStatement whileBlockSyntaxStatement -> whileBlockSyntaxStatement(whileBlockSyntaxStatement);
            case ForBlockSyntaxStatement forBlockSyntaxStatement -> forBlockSyntaxStatement(forBlockSyntaxStatement);
            case MethodDeclarationSyntaxStatement methodDeclarationSyntaxStatement -> methodDeclarationSyntaxStatement(methodDeclarationSyntaxStatement);
            default -> unAllowedTopLevelStatement(statement);
        };
    }

    private BoundStatement unAllowedTopLevelStatement(SyntaxStatement statement) {
        BoundStatement boundStatement = blockLevelSyntaxStatement(statement);
        diagnostics.addDiagnostic(boundStatement.startIndex(), boundStatement.endIndex(), boundStatement.lineNumber(), "statement of type `%s` is not allowed at top level", statement.statementType());
        return boundStatement;
    }

    private BoundStatement blockLevelSyntaxStatement(SyntaxStatement statement) {
        return switch (statement) {
            case BlockSyntaxStatement blockSyntaxStatement -> blockSyntaxStatement(blockSyntaxStatement);
            case ExpressionSyntaxStatement expressionSyntaxStatement -> expressionSyntaxStatement(expressionSyntaxStatement);
            case VariableDeclarationSyntaxStatement variableDeclarationSyntaxStatement -> variableDeclarationSyntaxStatement(variableDeclarationSyntaxStatement);
            case VariableReassignmentSyntaxStatement variableReassignmentSyntaxStatement -> variableReassignmentSyntaxStatement(variableReassignmentSyntaxStatement);
            case IfBlockSyntaxStatement ifBlockSyntaxStatement -> ifBlockSyntaxStatement(ifBlockSyntaxStatement);
            case WhileBlockSyntaxStatement whileBlockSyntaxStatement -> whileBlockSyntaxStatement(whileBlockSyntaxStatement);
            case ForBlockSyntaxStatement forBlockSyntaxStatement -> forBlockSyntaxStatement(forBlockSyntaxStatement);
            case ReturnSyntaxStatement returnSyntaxStatement -> returnSyntaxStatement(returnSyntaxStatement);
            default -> unAllowedBlockLevelStatement(statement);
        };
    }

    private BoundStatement unAllowedBlockLevelStatement(SyntaxStatement statement) {
        BoundStatement boundStatement = switch (statement) {
            case MethodDeclarationSyntaxStatement methodDeclarationSyntaxStatement -> methodDeclarationSyntaxStatement(methodDeclarationSyntaxStatement);
            default -> throw new RuntimeException("Unhandled statement type: " + statement.statementType());
        };
        diagnostics.addDiagnostic(boundStatement.startIndex(), boundStatement.endIndex(), boundStatement.lineNumber(), "statement of type `%s` is not allowed inside blocks", statement.statementType());
        return boundStatement;
    }

    private ReturnBoundStatement returnSyntaxStatement(ReturnSyntaxStatement returnSyntaxStatement) {
        BoundExpression expression = syntaxExpression(returnSyntaxStatement.expression());

        if (expression.type() != scope.currentMethodType() && scope.currentMethodType() != Type.UNKNOWN) {
            diagnostics.addDiagnostic(returnSyntaxStatement.returnToken().startIndex(), returnSyntaxStatement.semiColonToken().endIndex(), returnSyntaxStatement.returnToken().lineNumber(), "Expected type `%s`, got `%s`", scope.currentMethodType(), expression.type());
        }
        return new ReturnBoundStatement(returnSyntaxStatement.returnToken(), expression, returnSyntaxStatement.semiColonToken());
    }

    private MethodDeclarationBoundStatement methodDeclarationSyntaxStatement(MethodDeclarationSyntaxStatement methodDeclarationSyntaxStatement) {
        Optional<MethodDeclarationBoundStatement> alreadyDeclaredMethod = findMethod(methodDeclarationSyntaxStatement.methodNameToken().value(), null);
        if (alreadyDeclaredMethod.isPresent()) {
            diagnostics.addDiagnostic(
                    methodDeclarationSyntaxStatement.methodNameToken().startIndex(),
                    methodDeclarationSyntaxStatement.returnTypeToken().endIndex(),
                    methodDeclarationSyntaxStatement.methodNameToken().lineNumber(),
                    "Method with name `%s` is already defined",
                    methodDeclarationSyntaxStatement.methodNameToken().value()
            );
            return alreadyDeclaredMethod.get();
        }

        ParametersSyntax parameters = methodDeclarationSyntaxStatement.parameters();
        ParametersBound parametersBound = parametersSyntax(parameters);

        scope.newScope();

        for (ParameterBound parameter : parametersBound.parameters()) {
            scope.addVariable(parameter.parameterNameToken().value(), parameter.type());
        }

        Type returnType = Type.fromName(methodDeclarationSyntaxStatement.returnTypeToken().value());
        if (returnType == Type.UNKNOWN) {
            diagnostics.addDiagnostic(methodDeclarationSyntaxStatement.returnTypeToken().startIndex(),
                    methodDeclarationSyntaxStatement.returnTypeToken().endIndex(),
                    methodDeclarationSyntaxStatement.returnTypeToken().lineNumber(),
                    "Invalid return type `%s`", methodDeclarationSyntaxStatement.returnTypeToken().value());
        }
        scope.addMethodType(returnType);

        BlockBoundStatement body = blockSyntaxStatement(methodDeclarationSyntaxStatement.methodBody());

        MethodDeclarationBoundStatement methodDeclarationBoundStatement = new MethodDeclarationBoundStatement(
                methodDeclarationSyntaxStatement.methodNameToken(),
                methodDeclarationSyntaxStatement.openBracketToken(),
                parametersBound,
                methodDeclarationSyntaxStatement.closedBracketToken(),
                body,
                methodDeclarationSyntaxStatement.colonToken(),
                returnType
        );

        scope.removeMethodType();
        scope.remove();

        scope.addMethod(methodDeclarationBoundStatement.methodNameToken().value(), methodDeclarationBoundStatement);
        return methodDeclarationBoundStatement;
    }

    private ParametersBound parametersSyntax(ParametersSyntax parameters) {
        List<ParameterBound> parameterBounds = new ArrayList<>();
        Set<String> parameterNames = new HashSet<>();
        for (ParameterSyntax parameter : parameters.parameters()) {
            ParameterBound parameterBound = parameterSyntax(parameter);
            parameterBounds.add(parameterBound);

            String parameterName = parameterBound.parameterNameToken().value();
            if (parameterNames.contains(parameterBound.parameterNameToken().value())) {
                diagnostics.addDiagnostic(parameterBound.parameterNameToken(), "Parameter name `%s` is already used", parameterName);
            }
            parameterNames.add(parameterName);
        }
        return new ParametersBound(parameterBounds);
    }

    private ParameterBound parameterSyntax(ParameterSyntax parameter) {
        Type type = Type.fromName(parameter.typeToken().value());
        return new ParameterBound(parameter.parameterNameToken(), parameter.semiColonToken(), type, parameter.commaToken());
    }

    private ForBlockBoundStatement forBlockSyntaxStatement(ForBlockSyntaxStatement forBlockSyntaxStatement) {
        scope.newScope();
        BoundStatement initializer = blockLevelSyntaxStatement(forBlockSyntaxStatement.initializer());

        ExpressionBoundStatement condition = expressionSyntaxStatement(forBlockSyntaxStatement.condition());
        if (!condition.expression().type().equals(Type.BOOLEAN)) {
            diagnostics.addDiagnostic(forBlockSyntaxStatement.forKeywordToken(), "condition in `for` loop should evaluate to a boolean, but got `%s`", condition.expression().type());
        }

        BoundStatement stepper = blockLevelSyntaxStatement(forBlockSyntaxStatement.stepper());
        BlockBoundStatement body = blockSyntaxStatement(forBlockSyntaxStatement.forBlockBody());

        scope.remove();

        return new ForBlockBoundStatement(forBlockSyntaxStatement.forKeywordToken(), forBlockSyntaxStatement.openBracket(), initializer, condition, stepper, forBlockSyntaxStatement.closedBracket(), body);
    }

    private WhileBlockBoundStatement whileBlockSyntaxStatement(WhileBlockSyntaxStatement whileBlockSyntaxStatement) {
        BoundExpression condition = syntaxExpression(whileBlockSyntaxStatement.condition());
        if(!condition.type().equals(Type.BOOLEAN)) {
            diagnostics.addDiagnostic(whileBlockSyntaxStatement.whileKeywordToken(), "Condition inside `while` should evaluate to a boolean, but got `%s`", condition.type());
        }
        BlockBoundStatement body = blockSyntaxStatement(whileBlockSyntaxStatement.whileBlockBody());
        return new WhileBlockBoundStatement(
                whileBlockSyntaxStatement.whileKeywordToken(),
                whileBlockSyntaxStatement.openBracket(),
                condition,
                whileBlockSyntaxStatement.closedBracket(),
                body
        );
    }

    private IfBlockBoundStatement ifBlockSyntaxStatement(IfBlockSyntaxStatement ifBlockSyntaxStatement) {
        BoundExpression condition = syntaxExpression(ifBlockSyntaxStatement.condition());
        if(!condition.type().equals(Type.BOOLEAN)) {
            diagnostics.addDiagnostic(ifBlockSyntaxStatement.ifKeywordToken(), "Condition inside `if` should evaluate to a boolean, but got `%s`", condition.type());
        }
        BlockBoundStatement body = blockSyntaxStatement(ifBlockSyntaxStatement.ifBlockBody());
        Optional<ElseBlockBoundStatement> elseBody = ifBlockSyntaxStatement.elseBlockBody()
                .map(this::elseBlockSyntaxStatement);
        return new IfBlockBoundStatement(
                ifBlockSyntaxStatement.ifKeywordToken(),
                ifBlockSyntaxStatement.openBracket(),
                condition,
                ifBlockSyntaxStatement.closedBracket(),
                body,
                elseBody
        );
    }

    private ElseBlockBoundStatement elseBlockSyntaxStatement(ElseBlockSyntaxStatement elseBlockSyntaxStatement) {
        BlockBoundStatement elseBlockBody = blockSyntaxStatement(elseBlockSyntaxStatement.elseBlockBody());
        return new ElseBlockBoundStatement(elseBlockSyntaxStatement.elseKeywordToken(), elseBlockBody);
    }

    private VariableReassignmentBoundStatement variableReassignmentSyntaxStatement(VariableReassignmentSyntaxStatement variableReassignmentSyntaxStatement) {
        String name = variableReassignmentSyntaxStatement.identifierToken().value();
        Optional<Type> typeOptional = scope.typeOfVariable(variableReassignmentSyntaxStatement.identifierToken().value());
        if (typeOptional.isEmpty()) {
            diagnostics.addDiagnostic(variableReassignmentSyntaxStatement.identifierToken(), "Variable '%s' is not defined", name);
        }
        BoundExpression expression = syntaxExpression(variableReassignmentSyntaxStatement.expression());

        Type type = typeOptional.orElseGet(expression::type);

        if (!expression.type().equals(type)) {
            diagnostics.addDiagnostic(variableReassignmentSyntaxStatement.identifierToken(), "Assigned variable('%s') type is %s, declared with %s", name, typeOptional.get(), expression.type());
        }
        return new VariableReassignmentBoundStatement(
                variableReassignmentSyntaxStatement.identifierToken(),
                variableReassignmentSyntaxStatement.equalsToken(),
                expression,
                variableReassignmentSyntaxStatement.semiColonToken()
        );
    }

    private BoundStatement variableDeclarationSyntaxStatement(VariableDeclarationSyntaxStatement variableDeclarationSyntaxStatement) {
        String name = variableDeclarationSyntaxStatement.identifierToken().value();
        if (scope.isVariableDeclared(name)) {
            diagnostics.addDiagnostic(
                    variableDeclarationSyntaxStatement.letToken().startIndex(),
                    variableDeclarationSyntaxStatement.identifierToken().endIndex(),
                    variableDeclarationSyntaxStatement.letToken().lineNumber(),
                    "Variable '%s' is already defined", name);
        }

        Token colonToken = getOrDefaultToken(variableDeclarationSyntaxStatement.colonToken(), ":", TokenType.COLON_TOKEN);

        BoundExpression expression = syntaxExpression(variableDeclarationSyntaxStatement.expression());
        Type type = variableDeclarationSyntaxStatement.typeToken() == null ? expression.type() : Type.fromName(variableDeclarationSyntaxStatement.typeToken().value());

        if (!expression.type().equals(type)) {
            diagnostics.addDiagnostic(variableDeclarationSyntaxStatement.typeToken(), "Declared type `%s` but got `%s`", type, expression.type());
        }

        scope.addVariable(name, expression.type());
        return new VariableDeclarationBoundStatement(
                variableDeclarationSyntaxStatement.letToken(),
                variableDeclarationSyntaxStatement.identifierToken(),
                colonToken,
                expression.type(),
                variableDeclarationSyntaxStatement.equalsToken(),
                expression,
                variableDeclarationSyntaxStatement.semiColonToken()
        );
    }

    private Token getOrDefaultToken(Token token, String defaultValue, TokenType type) {
        return token == null ? new Token(defaultValue, type, 0, 0, 0) : token;
    }

    private ExpressionBoundStatement expressionSyntaxStatement(ExpressionSyntaxStatement expressionSyntaxStatement) {
        BoundExpression expression = syntaxExpression(expressionSyntaxStatement.expression());
        return new ExpressionBoundStatement(expression, expressionSyntaxStatement.semiColonToken());
    }

    private BlockBoundStatement blockSyntaxStatement(BlockSyntaxStatement blockSyntaxStatement) {
        scope.newScope();
        ArrayList<BoundStatement> statements = new ArrayList<>();
        for (SyntaxStatement statement: blockSyntaxStatement.statements()) {
            BoundStatement boundStatement = blockLevelSyntaxStatement(statement);
            if (boundStatement != null) {
                statements.add(boundStatement);
            }
        }
        scope.remove();
        return new BlockBoundStatement(blockSyntaxStatement.openBracket(), blockSyntaxStatement.closedBracket(), statements);
    }

    public BoundExpression syntaxExpression(SyntaxExpression syntaxExpression) {
        return switch (syntaxExpression) {
            case LiteralSyntaxExpression literalExpression -> literalExpression(literalExpression);
            case UnarySyntaxExpression unaryExpression -> unaryExpression(unaryExpression);
            case BinarySyntaxExpression binaryExpression -> binaryExpression(binaryExpression);
            case MethodCallSyntaxExpression methodCallSyntaxExpression -> methodCallSyntaxExpression(methodCallSyntaxExpression);
            default -> throw new RuntimeException("Unhandled expression: " + syntaxExpression.expressionType());
        };
    }

    private MethodCallBoundExpression methodCallSyntaxExpression(MethodCallSyntaxExpression methodCallSyntaxExpression) {
        ArgumentsBound argumentsBound = argumentsSyntax(methodCallSyntaxExpression.argumentsSyntax());

        MethodDeclarationBoundStatement method = findMethod(methodCallSyntaxExpression.methodName().value(), argumentsBound)
                .orElseGet(() -> {
                    diagnostics.addDiagnostic(methodCallSyntaxExpression.methodName().startIndex(), methodCallSyntaxExpression.methodName().endIndex(), methodCallSyntaxExpression.methodName().lineNumber(), "Method %s does not exist", methodCallSyntaxExpression.methodName().value());
                    return null;
                });

        if (method == null || method.methodNameToken().value().equals("println")) {
            return new MethodCallBoundExpression(
                    methodCallSyntaxExpression.methodName(),
                    methodCallSyntaxExpression.openBrace(),
                    argumentsBound,
                    methodCallSyntaxExpression.closedBrace(),
                    Type.STRING
            );
        }
        List<ArgumentBoundExpression> arguments = argumentsBound.expressions();
        List<ParameterBound> parameters = method.parametersBound().parameters();

        for (int i = 0; i < parameters.size(); i++) {
            ParameterBound parameter = parameters.get(i);
            if (arguments.size() <= i) {
                Token closedBrace = methodCallSyntaxExpression.closedBrace();
                diagnostics.addDiagnostic(closedBrace.startIndex(), closedBrace.endIndex(), closedBrace.lineNumber(), "Expected %d arguments but got %s", parameters.size(), arguments.size());
                break;
            }
            ArgumentBoundExpression argument = arguments.get(i);
            if (argument.expression().type() != parameter.type()) {
                diagnostics.addDiagnostic(argument.startIndex(), argument.endIndex(), argument.lineNumber(), "Expected `%s` but got `%s`", parameter.type(), argument.type());
            }
        }

        for (int i = parameters.size(); i < arguments.size(); i++) {
            ArgumentBoundExpression argument = arguments.get(i);
            diagnostics.addDiagnostic(argument.startIndex(), argument.endIndex(), argument.lineNumber(), "Unknown argument");
        }

        return new MethodCallBoundExpression(
                methodCallSyntaxExpression.methodName(),
                methodCallSyntaxExpression.openBrace(),
                argumentsBound,
                methodCallSyntaxExpression.closedBrace(),
                method.returnType()
        );
    }

    private Optional<MethodDeclarationBoundStatement> findMethod(String methodName, ArgumentsBound arguments) {
        return scope.findMethod(methodName);
    }

    private ArgumentsBound argumentsSyntax(ArgumentsSyntax argumentsSyntax) {
        List<ArgumentBoundExpression> expressions = new ArrayList<>();
        for (ArgumentSyntax argumentSyntax : argumentsSyntax.argumentSyntaxes()) {
            ArgumentBoundExpression expression = argumentSyntax(argumentSyntax);
            expressions.add(expression);
        }
        return new ArgumentsBound(expressions);
    }

    private ArgumentBoundExpression argumentSyntax(ArgumentSyntax argumentSyntax) {
        BoundExpression expression = syntaxExpression(argumentSyntax.expression());
        return new ArgumentBoundExpression(expression, argumentSyntax.commaToken());
    }

    private BinaryBoundExpression binaryExpression(BinarySyntaxExpression binaryExpression) {
        BoundExpression left = syntaxExpression(binaryExpression.left());
        BoundExpression right = syntaxExpression(binaryExpression.right());
        Token operatorToken = binaryExpression.token();

        if (left.type().equals(Type.UNKNOWN) || right.type().equals(Type.UNKNOWN)) {
            return new BinaryBoundExpression(left, right, binaryExpression.token(), Type.UNKNOWN);
        }

        if (!left.type().equals(right.type())) {
            diagnostics.addDiagnostic(operatorToken, "Operator '%s' can not be used with '%s' and '%s'", operatorToken.value(), left.type(), right.type());
            return new BinaryBoundExpression(left, right, binaryExpression.token(), Type.UNKNOWN);
        }

        if(binaryExpression.token().type() == TokenType.PLUS_TOKEN) {
            if (left.type().equals(Type.STRING) && right.type().equals(Type.STRING)) {
                return new BinaryBoundExpression(left, right, binaryExpression.token(), Type.STRING);
            } else if (left.type().equals(Type.STRING) || right.type().equals(Type.STRING)) {
                diagnostics.addDiagnostic(operatorToken, "Operator '%s' can not be used with '%s' and '%s'", binaryExpression.token().value(), left.type(), right.type());
                return new BinaryBoundExpression(left, right, binaryExpression.token(), Type.UNKNOWN);
            }
        }

        if (binaryExpression.token().type().isMathematicalOperatorToken()) {
            if (!left.type().equals(Type.INT) || !right.type().equals(Type.INT)) {
                diagnostics.addDiagnostic(operatorToken, "Operator '%s' can not be used with '%s' and '%s'", binaryExpression.token().value(), left.type(), right.type());
                return new BinaryBoundExpression(left, right, binaryExpression.token(), Type.UNKNOWN);
            }
            return new BinaryBoundExpression(left, right, binaryExpression.token(), Type.INT);
        }

        if (binaryExpression.token().type().isOnlyBooleanLogicalOperator()) {
            if (!left.type().equals(Type.BOOLEAN) || !right.type().equals(Type.BOOLEAN)) {
                diagnostics.addDiagnostic(operatorToken, "Operator '%s' can not be used with '%s' and '%s'", binaryExpression.token().value(), left.type(), right.type());
                return new BinaryBoundExpression(left, right, binaryExpression.token(), Type.UNKNOWN);
            }
            return new BinaryBoundExpression(left, right, binaryExpression.token(), Type.BOOLEAN);
        }

        if (binaryExpression.token().type().isLogicalOperatorToken()) {
            return new BinaryBoundExpression(left, right, binaryExpression.token(), Type.BOOLEAN);
        }
        diagnostics.addDiagnostic(operatorToken, "Operator '%s' is not implemented to use with '%s' and '%s'", binaryExpression.token().value(), left.type(), right.type());
        return new BinaryBoundExpression(left, right, binaryExpression.token(), left.type());
    }

    private UnaryBoundExpression unaryExpression(UnarySyntaxExpression unaryExpression) {
        BoundExpression expression = syntaxExpression(unaryExpression.syntaxExpression());
        Token operator = unaryExpression.operator();

        switch (operator.type()) {
            case PLUS_TOKEN, MINUS_TOKEN -> {
                if (!expression.type().equals(Type.INT)) {
                    diagnostics.addDiagnostic(operator, "Operator '%s' is not valid for '%s'", operator.value(), expression.type());
                }
            }
            case BANG_TOKEN -> {
                if (!expression.type().equals(Type.BOOLEAN)) {
                    diagnostics.addDiagnostic(operator, "Operator '%s' is not valid for '%s'", operator.value(), expression.type());
                }
            }
        }
        return new UnaryBoundExpression(operator, expression.type(), expression);
    }

    private BoundExpression literalExpression(LiteralSyntaxExpression literalExpression) {
        Token token = literalExpression.token();

        if (token.type().equals(TokenType.NUMBER_TOKEN)) {
            return new LiteralBoundExpression(token, Type.INT);
        }
        if (token.type().equals(TokenType.KEYWORD_TRUE_TOKEN) || token.type().equals(TokenType.KEYWORD_FALSE_TOKEN)) {
            return new LiteralBoundExpression(token, Type.BOOLEAN);
        }
        if (token.type().equals(TokenType.STRING_TOKEN)) {
            return new LiteralBoundExpression(token, Type.STRING);
        }
        if (token.type() == TokenType.IDENTIFIER_TOKEN) {
            Optional<Type> type = scope.typeOfVariable(token.value());
            if (type.isEmpty()) {
                this.diagnostics.addDiagnostic(token, "Variable '%s' is not in scope", token.value());
                return new BoundExpression.NoOpBoundExpression(token.value(), token.startIndex(), token.endIndex(), token.lineNumber());
            }
            return new LiteralBoundExpression(token, type.get());
        }

        this.diagnostics.addDiagnostic(token, "Unknown syntax '%s'", token.value());
        return new BoundExpression.NoOpBoundExpression(token.value(), token.startIndex(), token.endIndex(), token.lineNumber());
    }

    private void addGlobalMethods() {
        scope.addMethod("input", new MethodDeclarationBoundStatement(new Token("input", TokenType.IDENTIFIER_TOKEN, 0 , 0, 0), null, new ParametersBound(Collections.emptyList()), null, null, null, Type.STRING));
        scope.addMethod("println", new MethodDeclarationBoundStatement(new Token("println", TokenType.IDENTIFIER_TOKEN, 0 , 0, 0), null, null, null, null, null, Type.STRING));
    }

    public Diagnostics diagnostics() {
        return diagnostics;
    }
}
