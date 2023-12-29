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
        String expression1 = "var a = 5; a = a + 10";

        String expression = """
                {
                    var a = 11;
                    {
                       for(var b = 5; b < 10; b = b + 2) {
                            a = a + 2;
                       }
                       
                       for(var b = 15; b < 20; b = b + 1) {
                            a = a + 2;
                       }
                    }
                    a = a;
                }
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
