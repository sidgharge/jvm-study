package come.homeproects.jvmstudy.parser.statements;

import come.homeproects.jvmstudy.parser.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

public record BlockSyntaxStatement(Token openBracket, Token closedBracket, List<SyntaxStatement> statements) implements SyntaxStatement {

    @Override
    public StatementType statementType() {
        return StatementType.BLOCK_STATEMENT;
    }

    @Override
    public String toString() {
        return printString(0);
    }

    @Override
    public String printString(int indent) {
        String tabs = "  ".repeat(indent);
        return statements.stream()
                .map(s -> s.printString(indent + 1))
                .collect(Collectors.joining("\n", tabs + "{\n", "\n" + tabs + "}"));
    }
}
