package come.homeproects.jvmstudy.parser.statements;

public interface SyntaxStatement {

    StatementType statementType();

    String prettyString(int indent);
}
