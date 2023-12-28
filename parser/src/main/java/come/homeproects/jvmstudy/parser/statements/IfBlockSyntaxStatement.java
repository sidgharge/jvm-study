package come.homeproects.jvmstudy.parser.statements;

import come.homeproects.jvmstudy.parser.expressions.SyntaxExpression;
import come.homeproects.jvmstudy.parser.lexer.Token;

import java.util.Optional;

public record IfBlockSyntaxStatement(
        Token ifKeywordToken,
        Token openBracket,
        SyntaxExpression condition,
        Token closedBracket,
        BlockSyntaxStatement ifBlockBody,
        Optional<ElseBlockSyntaxStatement> elseBlockBody) implements SyntaxStatement {

    @Override
    public StatementType statementType() {
        return StatementType.IF_STATEMENT;
    }

    @Override
    public String printString(int indent) {
        return "  ".repeat(indent) + String.format("%s %s%s%s\n%s%s",
                ifKeywordToken.value(),
                openBracket.value(),
                condition.toString(),
                closedBracket.value(),
                ifBlockBody.printString(indent),
                elseBlockBody.map(e -> e.printString(indent)).orElse(""));
    }
}
