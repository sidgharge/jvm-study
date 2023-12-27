package come.homeproects.jvmstudy.parser;

import come.homeproects.jvmstudy.parser.binder.Binder;
import come.homeproects.jvmstudy.parser.binder.statements.BoundStatement;
import come.homeproects.jvmstudy.parser.evaluator.Evaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.stream.Collectors;

public class ManualTest {

    @Test
    public void test() {
//        String expression = "2 *(3 + 4 * (5 + 6))";

        String expression = """
                {
                    var a = 5 * 6;
                }
                {
                    var a = a + 5;
                }
                a + 2;
                """;

        Binder binder = new Binder();
        BoundStatement statement = binder.bind(expression);

        System.out.println("Tokens:\n" + binder.tokens().stream().map(Objects::toString).collect(Collectors.joining(" ")));

        System.out.println("Parser AST:\n" + binder.syntaxStatement().printString(0));

        System.out.println("Binder AST:\n" + statement);

        if (binder.diagnostics().hasErrors()) {
            binder.diagnostics().errors().forEach(System.err::println);
            Assertions.fail();
            return;
        }
        System.out.println("--------------------------------------");
        System.out.println("result: " + new Evaluator().evaluate(statement));
    }
}