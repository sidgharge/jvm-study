package com.homeprojects.jvmstudy.parser.binder;

import com.homeprojects.jvmstudy.parser.Parser;
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
import com.homeprojects.jvmstudy.parser.lowerer.Lowerer;
import com.homeprojects.jvmstudy.parser.statements.ForBlockSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.VariableDeclarationSyntaxStatement;
import com.homeprojects.jvmstudy.parser.types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Binder {

    private final Diagnostics diagnostics;

    private final List<Map<String, Type>> scopedTypes;

    private List<Token> tokens;

    private SyntaxStatement syntaxStatement;

    private final Map<String, Type> methods;

    public Binder() {
        this.diagnostics = new Diagnostics();
        this.scopedTypes = new ArrayList<>();
        this.methods = new HashMap<>();
        addGlobalMethods();
    }

    public BoundStatement bind(String inputExpression) {
        Parser parser = new Parser(inputExpression);
        this.syntaxStatement = parser.parse();
        this.tokens = parser.tokens();
        parser.diagnostics().errors().forEach(diagnostics::add);
        BoundStatement boundStatement = bind(syntaxStatement);
        return new Lowerer().lower(boundStatement);
    }

    private BoundStatement bind(SyntaxStatement statement) {
        return switch (statement) {
            case BlockSyntaxStatement blockSyntaxStatement -> blockSyntaxStatement(blockSyntaxStatement);
            case ExpressionSyntaxStatement expressionSyntaxStatement -> expressionSyntaxStatement(expressionSyntaxStatement);
            case VariableDeclarationSyntaxStatement variableDeclarationSyntaxStatement -> variableDeclarationSyntaxStatement(variableDeclarationSyntaxStatement);
            case VariableReassignmentSyntaxStatement variableReassignmentSyntaxStatement -> variableReassignmentSyntaxStatement(variableReassignmentSyntaxStatement);
            case IfBlockSyntaxStatement ifBlockSyntaxStatement -> ifBlockSyntaxStatement(ifBlockSyntaxStatement);
            case WhileBlockSyntaxStatement whileBlockSyntaxStatement -> whileBlockSyntaxStatement(whileBlockSyntaxStatement);
            case ForBlockSyntaxStatement forBlockSyntaxStatement -> forBlockSyntaxStatement(forBlockSyntaxStatement);
            default -> throw new RuntimeException("Unhandled statement type: " + statement.statementType());
        };
    }

    private ForBlockBoundStatement forBlockSyntaxStatement(ForBlockSyntaxStatement forBlockSyntaxStatement) {
        scopedTypes.add(new HashMap<>());
        BoundStatement initializer = bind(forBlockSyntaxStatement.initializer());

        ExpressionBoundStatement condition = expressionSyntaxStatement(forBlockSyntaxStatement.condition());
        if (!condition.expression().type().equals(Type.BOOLEAN)) {
            diagnostics.addDiagnostic(forBlockSyntaxStatement.forKeywordToken(), "condition in `for` loop should evaluate to a boolean, but got `%s`", condition.expression().type());
        }

        BoundStatement stepper = bind(forBlockSyntaxStatement.stepper());
        BlockBoundStatement body = blockSyntaxStatement(forBlockSyntaxStatement.forBlockBody());

        scopedTypes.removeLast();

        return new ForBlockBoundStatement(forBlockSyntaxStatement.forKeywordToken(), forBlockSyntaxStatement.openBracket(), initializer, condition, stepper, forBlockSyntaxStatement.closedBracket(), body);
    }

    private WhileBlockBoundStatement whileBlockSyntaxStatement(WhileBlockSyntaxStatement whileBlockSyntaxStatement) {
        BoundExpression condition = bind(whileBlockSyntaxStatement.condition());
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
        BoundExpression condition = bind(ifBlockSyntaxStatement.condition());
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
        Optional<Type> typeOptional = getTypeFromStack(variableReassignmentSyntaxStatement.identifierToken());
        if (typeOptional.isEmpty()) {
            diagnostics.addDiagnostic(variableReassignmentSyntaxStatement.identifierToken(), "Variable '%s' is not defined", name);
        }
        BoundExpression expression = bind(variableReassignmentSyntaxStatement.expression());

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
        Map<String, Type> types = scopedTypes.getLast();
        if (types.containsKey(name)) {
            diagnostics.addDiagnostic(variableDeclarationSyntaxStatement.letToken(), "Variable '%s' is already defined", name);
        }

        Token colonToken = getOrDefaultToken(variableDeclarationSyntaxStatement.colonToken(), ":", TokenType.COLON_TOKEN);

        BoundExpression expression = bind(variableDeclarationSyntaxStatement.expression());
        Type type = variableDeclarationSyntaxStatement.typeToken() == null ? expression.type() : Type.fromName(variableDeclarationSyntaxStatement.typeToken().value());

        if (!expression.type().equals(type)) {
            diagnostics.addDiagnostic(variableDeclarationSyntaxStatement.typeToken(), "Declared type `%s` but got `%s`", type, expression.type());
        }

        types.put(name, expression.type());
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
        BoundExpression expression = bind(expressionSyntaxStatement.expression());
        return new ExpressionBoundStatement(expression, expressionSyntaxStatement.semiColonToken());
    }

    private BlockBoundStatement blockSyntaxStatement(BlockSyntaxStatement blockSyntaxStatement) {
        scopedTypes.add(new HashMap<>());
        ArrayList<BoundStatement> statements = new ArrayList<>();
        for (SyntaxStatement statement: blockSyntaxStatement.statements()) {
            BoundStatement boundStatement = bind(statement);
            if (boundStatement != null) {
                statements.add(boundStatement);
            }
        }
        scopedTypes.removeLast();
        return new BlockBoundStatement(blockSyntaxStatement.openBracket(), blockSyntaxStatement.closedBracket(), statements);
    }

    public BoundExpression bind(SyntaxExpression syntaxExpression) {
        return switch (syntaxExpression) {
            case LiteralSyntaxExpression literalExpression -> literalExpression(literalExpression);
            case UnarySyntaxExpression unaryExpression -> unaryExpression(unaryExpression);
            case BinarySyntaxExpression binaryExpression -> binaryExpression(binaryExpression);
            case MethodCallSyntaxExpression methodCallSyntaxExpression -> methodCallSyntaxExpression(methodCallSyntaxExpression);
            default -> throw new RuntimeException("Unhandled expression: " + syntaxExpression.expressionType());
        };
    }

    private MethodCallBoundExpression methodCallSyntaxExpression(MethodCallSyntaxExpression methodCallSyntaxExpression) {
        ArgumentsBound arguments = argumentsSyntax(methodCallSyntaxExpression.argumentsSyntax());

        Type type = findMethod(methodCallSyntaxExpression.methodName().value(), arguments);

        return new MethodCallBoundExpression(
                methodCallSyntaxExpression.methodName(),
                methodCallSyntaxExpression.openBrace(),
                arguments,
                methodCallSyntaxExpression.closedBrace(),
                type
        );
    }

    private Type findMethod(String methodName, ArgumentsBound arguments) {
        return methods.getOrDefault(methodName, Type.UNKNOWN);
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
        BoundExpression expression = bind(argumentSyntax.expression());
        return new ArgumentBoundExpression(expression, argumentSyntax.commaToken());
    }

    private BinaryBoundExpression binaryExpression(BinarySyntaxExpression binaryExpression) {
        BoundExpression left = bind(binaryExpression.left());
        BoundExpression right = bind(binaryExpression.right());
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
        BoundExpression expression = bind(unaryExpression.syntaxExpression());
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
            Optional<Type> type = getTypeFromStack(token);
            if (type.isEmpty()) {
                this.diagnostics.addDiagnostic(token, "Variable '%s' is not in scope", token.value());
                return new BoundExpression.NoOpBoundExpression(token.value());
            }
            return new LiteralBoundExpression(token, type.get());
        }

        this.diagnostics.addDiagnostic(token, "Unknown syntax '%s'", token.value());
        return new BoundExpression.NoOpBoundExpression(token.value());
    }

    private Optional<Type> getTypeFromStack(Token token) {
        for (int i = scopedTypes.size() - 1; i >= 0; i--) {
            if (scopedTypes.get(i).containsKey(token.value())) {
                return Optional.of(scopedTypes.get(i).get(token.value()));
            }
        }
        return Optional.empty();
    }

    private void addGlobalMethods() {
        methods.put("input", Type.STRING);
        methods.put("println", Type.VOID);
    }

    public Diagnostics diagnostics() {
        return diagnostics;
    }

    public List<Token> tokens() {
        return tokens;
    }

    public SyntaxStatement syntaxStatement() {
        return syntaxStatement;
    }
}
