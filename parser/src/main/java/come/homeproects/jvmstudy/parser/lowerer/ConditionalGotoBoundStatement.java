package come.homeproects.jvmstudy.parser.lowerer;

import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression;
import come.homeproects.jvmstudy.parser.binder.statements.BoundStatement;

public record ConditionalGotoBoundStatement(BoundExpression condition, Label otherwiseLabel) implements BoundStatement {

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return String.format("%sif %s else goto %s", "  ".repeat(indent), condition.toString(), otherwiseLabel.toString());
    }
}
