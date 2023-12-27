package come.homeproects.jvmstudy.parser.statements;

import come.homeproects.jvmstudy.parser.expressions.SyntaxExpression;
import come.homeproects.jvmstudy.parser.lexer.Token;

public record VariableDeclarationSyntaxStatement(
        Token varToken,
        Token identifierToken,
        Token equalsToken,
        SyntaxExpression expression,
        Token semiColonToken) implements SyntaxStatement {

    @Override
    public StatementType statementType() {
        return StatementType.VARIABLE_DECLARATION_STATEMENT;
    }

    @Override
    public String printString(int indent) {
        return "".repeat(indent) + String.format("%s %s %s %s%s",
                varToken.value(),
                identifierToken.value(),
                equalsToken.value(),
                expression.toString(),
                semiColonToken.value());
    }
}