package come.homeproects.jvmstudy.parser.lowerer;

import come.homeproects.jvmstudy.parser.binder.statements.BlockBoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.BoundStatement;
import come.homeproects.jvmstudy.parser.binder.statements.IfBlockBoundStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Lowerer {

    private int labelCounter;

    public Lowerer() {
        this.labelCounter = 0;
    }

    public BoundStatement lower(BoundStatement boundStatement) {
        return switch (boundStatement) {
            case BlockBoundStatement blockBoundStatement -> blockBoundStatement(blockBoundStatement);
            case IfBlockBoundStatement ifBlockBoundStatement -> ifBlockBoundStatement(ifBlockBoundStatement);
            default -> boundStatement;
        };
    }

    private BlockBoundStatement blockBoundStatement(BlockBoundStatement blockBoundStatement) {
        List<BoundStatement> statements = blockBoundStatement.statements().stream().map(this::lower).collect(Collectors.toList());
        if (statements.equals(blockBoundStatement.statements())) {
            return blockBoundStatement;
        }
        return new BlockBoundStatement(blockBoundStatement.openBrace(), blockBoundStatement.closedBrace(), statements);
    }

    private BoundStatement ifBlockBoundStatement(IfBlockBoundStatement ifStatement) {
        List<BoundStatement> statements = new ArrayList<>();

        Label endLabel = nextLabel();
        Label elseLabel = ifStatement.elseBlockBody().isPresent() ? nextLabel() : endLabel;
        LabelBoundStatement endStatement = new LabelBoundStatement(endLabel);
        ConditionalGotoBoundStatement conditionalGotoBoundStatement = new ConditionalGotoBoundStatement(ifStatement.condition(), elseLabel);

        statements.add(conditionalGotoBoundStatement);
        statements.addAll(blockBoundStatement(ifStatement.ifBlockBody()).statements());

        if (ifStatement.elseBlockBody().isPresent()) {
            statements.add(new GotoBoundStatement(endLabel));
            statements.add(new LabelBoundStatement(elseLabel));
            statements.addAll(blockBoundStatement(ifStatement.elseBlockBody().get().elseBlockBody()).statements());
        }
        statements.add(endStatement);

        return new BlockBoundStatement(ifStatement.ifBlockBody().openBrace(), ifStatement.ifBlockBody().closedBrace(), statements);
    }

    private Label nextLabel() {
        return new Label("label_" + labelCounter++);
    }
}
