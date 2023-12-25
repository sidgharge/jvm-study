package come.homeproects.jvmstudy.parser.bindexpressions;

import come.homeproects.jvmstudy.parser.diagnostics.Diagnostics;
import come.homeproects.jvmstudy.parser.Parser;
import come.homeproects.jvmstudy.parser.expressions.BinaryExpression;
import come.homeproects.jvmstudy.parser.expressions.Expression;
import come.homeproects.jvmstudy.parser.expressions.LiteralExpression;
import come.homeproects.jvmstudy.parser.expressions.UnaryExpression;
import come.homeproects.jvmstudy.parser.lexer.Token;

import java.lang.reflect.Type;

public class Binder {

    private final Diagnostics diagnostics;

    public Binder() {
        diagnostics = new Diagnostics();
    }

    public BoundExpression bind(String inputExpression) {
        return bind(inputExpression, false);
    }

    public BoundExpression bind(String inputExpression, boolean debug) {
        Parser parser = new Parser(inputExpression, debug);
        Expression expression = parser.parse();
        parser.diagnostics().errors().forEach(diagnostics::add);
        return bind(expression);
    }

    public Diagnostics diagnostics() {
        return diagnostics;
    }

    public BoundExpression bind(Expression expression) {
        return switch (expression) {
            case LiteralExpression literalExpression -> literalExpression(literalExpression);
            case UnaryExpression unaryExpression -> unaryExpression(unaryExpression);
            case BinaryExpression binaryExpression -> binaryExpression(binaryExpression);
            default -> throw new RuntimeException("Unhandled expression: " + expression.expressionType());
        };
    }

    private BinaryBoundExpression binaryExpression(BinaryExpression binaryExpression) {
        BoundExpression left = bind(binaryExpression.left());
        BoundExpression right = bind(binaryExpression.right());
        Token operatorToken = binaryExpression.token();
        if (!left.type().equals(right.type())) {
            diagnostics.addDiagnostic(operatorToken.startIndex(), operatorToken.endIndex(), "Operand '%s' can not be used with '%s' and '%s'", operatorToken.value(), left.type(), right.type());
        }
        if (binaryExpression.token().type().isMathematicalOperatorToken()) {
            if (!left.type().equals(int.class) || !right.type().equals(int.class)) {
                diagnostics.addDiagnostic(operatorToken.startIndex(), operatorToken.endIndex(), "Operand '%s' can not be used with '%s' and '%s'", binaryExpression.token().value(), left.type(), right.type());
            }
            return new BinaryBoundExpression(left, right, binaryExpression.token(), left.type());
        }
        if (binaryExpression.token().type().isLogicalOperatorToken()) {
            return new BinaryBoundExpression(left, right, binaryExpression.token(), boolean.class);
        }

        diagnostics.addDiagnostic(operatorToken.startIndex(), operatorToken.endIndex(), "Operand '%s' is not implemented to use with '%s' and '%s'", binaryExpression.token().value(), left.type(), right.type());
        return new BinaryBoundExpression(left, right, binaryExpression.token(), left.type());
    }

    private UnaryBoundExpression unaryExpression(UnaryExpression unaryExpression) {
        BoundExpression expression = bind(unaryExpression.expression());
        Token operator = unaryExpression.operator();

        switch (operator.type()) {
            case PLUS_TOKEN, MINUS_TOKEN -> {
                if (!expression.type().equals(int.class)) {
                    diagnostics.addDiagnostic(operator.startIndex(), operator.endIndex(), "Operator '%s' is not valid for '%s' at index %d", operator.value(), expression.type(), operator.startIndex());
                }
            }
            case BANG_TOKEN -> {
                if (!expression.type().equals(boolean.class)) {
                    diagnostics.addDiagnostic(operator.startIndex(), operator.endIndex(), "Operator '%s' is not valid for '%s' at index %d", operator.value(), expression.type(), operator.startIndex());
                }
            }
        }
        return new UnaryBoundExpression(operator, expression.type(), expression);
    }

    private LiteralBoundExpression literalExpression(LiteralExpression literalExpression) {
        Token token = literalExpression.token();
        Type type =  switch (token.type()) {
            case NUMBER_TOKEN -> int.class;
            case KEYWORD_TRUE_TOKEN, KEYWORD_FALSE_TOKEN -> boolean.class;
            default -> null;
        };
        if (type == null) {
            this.diagnostics.addDiagnostic(token.startIndex(), token.endIndex(), "Unknown syntax '%s' at index %d", token.value(), token.startIndex());
        }
        return new LiteralBoundExpression(token, type);
    }
}
