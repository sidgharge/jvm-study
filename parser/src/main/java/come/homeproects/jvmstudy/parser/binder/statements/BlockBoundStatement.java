package come.homeproects.jvmstudy.parser.binder.statements;

import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression;
import come.homeproects.jvmstudy.parser.lexer.Token;

import java.util.List;

public record BlockBoundStatement(Token openBrace, Token closedBrace, List<BoundStatement> statements) implements BoundStatement {
}
