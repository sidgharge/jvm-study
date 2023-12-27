package come.homeproects.jvmstudy.parser.binder.statements;

import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression;

public record ExpressionBoundStatement(BoundExpression expression) implements BoundStatement {

    @Override
    public String toString() {
        return expression.toString();
    }
}
