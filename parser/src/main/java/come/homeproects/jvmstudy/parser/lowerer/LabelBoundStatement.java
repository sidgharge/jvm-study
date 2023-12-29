package come.homeproects.jvmstudy.parser.lowerer;

import come.homeproects.jvmstudy.parser.binder.statements.BoundStatement;

public record LabelBoundStatement(Label label) implements BoundStatement {

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return "  ".repeat(indent) +  label.toString();
    }
}
