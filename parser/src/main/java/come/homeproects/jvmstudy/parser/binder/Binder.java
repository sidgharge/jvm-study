package come.homeproects.jvmstudy.parser.binder;

import come.homeproects.jvmstudy.parser.binder.expressions.BinaryBoundExpression;
import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression;
import come.homeproects.jvmstudy.parser.binder.expressions.LiteralBoundExpression;
import come.homeproects.jvmstudy.parser.binder.expressions.UnaryBoundExpression;
import come.homeproects.jvmstudy.parser.binder.statements.BlockBoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.BoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.ExpressionBoundStatement;
import come.homeproects.jvmstudy.parser.diagnostics.Diagnostics;
import come.homeproects.jvmstudy.parser.Parser;
import come.homeproects.jvmstudy.parser.expressions.BinarySyntaxExpression;
import come.homeproects.jvmstudy.parser.expressions.SyntaxExpression;
import come.homeproects.jvmstudy.parser.expressions.LiteralSyntaxExpression;
import come.homeproects.jvmstudy.parser.expressions.UnarySyntaxExpression;
import come.homeproects.jvmstudy.parser.lexer.Token;
import come.homeproects.jvmstudy.parser.lexer.TokenType;
import come.homeproects.jvmstudy.parser.statements.BlockSyntaxStatement;
import come.homeproects.jvmstudy.parser.statements.ExpressionSyntaxStatement;
import come.homeproects.jvmstudy.parser.statements.SyntaxStatement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Binder {

    private final Diagnostics diagnostics;

    private final Map<String, Type> types;

    public Binder() {
        diagnostics = new Diagnostics();
        types = new HashMap<>();
    }

    public BoundStatement bind(String inputExpression) {
        return bind(inputExpression, false);
    }

    public BoundStatement bind(String inputExpression, boolean debug) {
        Parser parser = new Parser(inputExpression, debug);
        SyntaxStatement statement = parser.parse();
        parser.diagnostics().errors().forEach(diagnostics::add);
        return bind(statement);
    }

    private BoundStatement bind(SyntaxStatement statement) {
        return switch (statement) {
            case BlockSyntaxStatement blockSyntaxStatement -> blockSyntaxStatement(blockSyntaxStatement);
            case ExpressionSyntaxStatement expressionSyntaxStatement -> expressionSyntaxStatement(expressionSyntaxStatement);
            default -> throw new RuntimeException("Unhandled statement type: " + statement.statementType());
        };
    }

    private ExpressionBoundStatement expressionSyntaxStatement(ExpressionSyntaxStatement expressionSyntaxStatement) {
        BoundExpression expression = bind(expressionSyntaxStatement.expression());
        return new ExpressionBoundStatement(expression);
    }

    private BlockBoundStatement blockSyntaxStatement(BlockSyntaxStatement blockSyntaxStatement) {
        List<BoundStatement> statements = new ArrayList<>();
        for (SyntaxStatement statement: blockSyntaxStatement.statements()) {
            BoundStatement boundStatement = bind(statement);
            statements.add(boundStatement);
        }
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
        if (isVariableAssignment(binaryExpression)) {
            return variableAssignmentSyntaxExpression(binaryExpression);
        }
        BoundExpression left = bind(binaryExpression.left());
        BoundExpression right = bind(binaryExpression.right());
        Token operatorToken = binaryExpression.token();
        if (!left.type().equals(right.type())) {
            diagnostics.addDiagnostic(operatorToken, "Operand '%s' can not be used with '%s' and '%s'", operatorToken.value(), left.type(), right.type());
            return new BinaryBoundExpression(left, right, binaryExpression.token(), left.type());
        }
        if (binaryExpression.token().type().isMathematicalOperatorToken()) {
            if (!left.type().equals(int.class) || !right.type().equals(int.class)) {
                diagnostics.addDiagnostic(operatorToken, "Operand '%s' can not be used with '%s' and '%s'", binaryExpression.token().value(), left.type(), right.type());
            }
            return new BinaryBoundExpression(left, right, binaryExpression.token(), left.type());
        }
        if (binaryExpression.token().type().isLogicalOperatorToken()) {
            return new BinaryBoundExpression(left, right, binaryExpression.token(), boolean.class);
        }
        diagnostics.addDiagnostic(operatorToken, "Operand '%s' is not implemented to use with '%s' and '%s'", binaryExpression.token().value(), left.type(), right.type());
        return new BinaryBoundExpression(left, right, binaryExpression.token(), left.type());
    }

    private boolean isVariableAssignment(BinarySyntaxExpression binaryExpression) {
        return binaryExpression.token().type().equals(TokenType.EQUALS_TOKEN)
                && binaryExpression.left() instanceof LiteralSyntaxExpression
                && ((LiteralSyntaxExpression)binaryExpression.left()).token().type().equals(TokenType.IDENTIFIER_TOKEN);
    }

    private BinaryBoundExpression variableAssignmentSyntaxExpression(BinarySyntaxExpression binaryExpression) {
        BoundExpression right = bind(binaryExpression.right());
        Token token = ((LiteralSyntaxExpression) binaryExpression.left()).token();
        LiteralBoundExpression left = new LiteralBoundExpression(token, right.type());
        types.put(token.value(), left.type());
        return new BinaryBoundExpression(left, right, binaryExpression.token(), left.type());
    }

    private UnaryBoundExpression unaryExpression(UnarySyntaxExpression unaryExpression) {
        BoundExpression expression = bind(unaryExpression.syntaxExpression());
        Token operator = unaryExpression.operator();

        switch (operator.type()) {
            case PLUS_TOKEN, MINUS_TOKEN -> {
                if (!expression.type().equals(int.class)) {
                    diagnostics.addDiagnostic(operator, "Operator '%s' is not valid for '%s' at index %d", operator.value(), expression.type(), operator.startIndex());
                }
            }
            case BANG_TOKEN -> {
                if (!expression.type().equals(boolean.class)) {
                    diagnostics.addDiagnostic(operator, "Operator '%s' is not valid for '%s' at index %d", operator.value(), expression.type(), operator.startIndex());
                }
            }
        }
        return new UnaryBoundExpression(operator, expression.type(), expression);
    }

    private LiteralBoundExpression literalExpression(LiteralSyntaxExpression literalExpression) {
        Token token = literalExpression.token();
        Type type =  switch (token.type()) {
            case NUMBER_TOKEN -> int.class;
            case KEYWORD_TRUE_TOKEN, KEYWORD_FALSE_TOKEN -> boolean.class;
            case IDENTIFIER_TOKEN -> types.getOrDefault(token.value(), Object.class);
            default -> null;
        };
        if (type == null) {
            this.diagnostics.addDiagnostic(token, "Unknown syntax '%s' at index %d", token.value(), token.startIndex());
        }
        return new LiteralBoundExpression(token, type);
    }

    public Diagnostics diagnostics() {
        return diagnostics;
    }
}
