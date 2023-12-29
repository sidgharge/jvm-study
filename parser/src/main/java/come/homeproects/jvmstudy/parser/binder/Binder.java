package come.homeproects.jvmstudy.parser.binder;

import come.homeproects.jvmstudy.parser.Parser;
import come.homeproects.jvmstudy.parser.binder.expressions.BinaryBoundExpression;
import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression;
import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression.NoOpBoundExpression;
import come.homeproects.jvmstudy.parser.binder.expressions.LiteralBoundExpression;
import come.homeproects.jvmstudy.parser.binder.expressions.UnaryBoundExpression;
import come.homeproects.jvmstudy.parser.binder.statements.BlockBoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.BoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.ElseBlockBoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.ExpressionBoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.IfBlockBoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.VariableDeclarationBoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.VariableReassignmentBoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.WhileBlockBoundStatement;
import come.homeproects.jvmstudy.parser.diagnostics.Diagnostics;
import come.homeproects.jvmstudy.parser.expressions.BinarySyntaxExpression;
import come.homeproects.jvmstudy.parser.expressions.LiteralSyntaxExpression;
import come.homeproects.jvmstudy.parser.expressions.SyntaxExpression;
import come.homeproects.jvmstudy.parser.expressions.UnarySyntaxExpression;
import come.homeproects.jvmstudy.parser.lexer.Token;
import come.homeproects.jvmstudy.parser.lexer.TokenType;
import come.homeproects.jvmstudy.parser.lowerer.Lowerer;
import come.homeproects.jvmstudy.parser.statements.BlockSyntaxStatement;
import come.homeproects.jvmstudy.parser.statements.ElseBlockSyntaxStatement;
import come.homeproects.jvmstudy.parser.statements.ExpressionSyntaxStatement;
import come.homeproects.jvmstudy.parser.statements.IfBlockSyntaxStatement;
import come.homeproects.jvmstudy.parser.statements.SyntaxStatement;
import come.homeproects.jvmstudy.parser.statements.VariableDeclarationSyntaxStatement;
import come.homeproects.jvmstudy.parser.statements.VariableReassignmentSyntaxStatement;
import come.homeproects.jvmstudy.parser.statements.WhileBlockSyntaxStatement;
import come.homeproects.jvmstudy.parser.types.Type;

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

    public Binder() {
        this.diagnostics = new Diagnostics();
        this.scopedTypes = new ArrayList<>();
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
            default -> throw new RuntimeException("Unhandled statement type: " + statement.statementType());
        };
    }

    private BoundStatement whileBlockSyntaxStatement(WhileBlockSyntaxStatement whileBlockSyntaxStatement) {
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
            return null;
        }
        BoundExpression expression = bind(variableReassignmentSyntaxStatement.expression());

        if (!expression.type().equals(typeOptional.get())) {
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
            diagnostics.addDiagnostic(variableDeclarationSyntaxStatement.varToken(), "Variable '%s' is already defined", name);
            return null;
        }
        BoundExpression expression = bind(variableDeclarationSyntaxStatement.expression());
        types.put(name, expression.type());
        return new VariableDeclarationBoundStatement(
                variableDeclarationSyntaxStatement.varToken(),
                variableDeclarationSyntaxStatement.identifierToken(),
                variableDeclarationSyntaxStatement.equalsToken(),
                expression,
                variableDeclarationSyntaxStatement.semiColonToken()
        );
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
            default -> throw new RuntimeException("Unhandled expression: " + syntaxExpression.expressionType());
        };
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
        if (token.type() == TokenType.IDENTIFIER_TOKEN) {
            Optional<Type> type = getTypeFromStack(token);
            if (type.isEmpty()) {
                this.diagnostics.addDiagnostic(token, "Variable '%s' is not in scope", token.value());
                return new NoOpBoundExpression(token.value());
            }
            return new LiteralBoundExpression(token, type.get());
        }

        this.diagnostics.addDiagnostic(token, "Unknown syntax '%s'", token.value());
        return new NoOpBoundExpression(token.value());
    }

    private Optional<Type> getTypeFromStack(Token token) {
        for (int i = scopedTypes.size() - 1; i >= 0; i--) {
            if (scopedTypes.get(i).containsKey(token.value())) {
                return Optional.of(scopedTypes.get(i).get(token.value()));
            }
        }
        return Optional.empty();
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
