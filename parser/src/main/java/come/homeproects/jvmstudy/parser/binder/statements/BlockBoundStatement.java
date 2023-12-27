package come.homeproects.jvmstudy.parser.binder.statements;

import come.homeproects.jvmstudy.parser.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

public record BlockBoundStatement(Token openBrace, Token closedBrace, List<BoundStatement> statements) implements BoundStatement {

    @Override
    public String toString() {
        return statements.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n", "{\n", "\n}"));
    }
}
