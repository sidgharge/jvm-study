package com.homeprojects.jvmstudy.parser;

import com.homeprojects.jvmstudy.parser.lexer.Token;
import org.junit.jupiter.api.Assertions;

import java.util.Objects;
import java.util.stream.Collectors;

public class ManualTest {

    public static void main(String[] args) {
        new ManualTest().test();
    }

//    @Test
    public void test() {
        String expression1 = """
                let a = 10;
                """;

        String expression = """
                sum1(a: int, b: int): int {
                    return a + b;
                }
                
                println("sum1(10, 20) =", sum1(10, 20));
                """;

        Runner runner = new Runner(expression);
        runner.run(false);

        System.out.println("Tokens:\n" + runner.tokens().stream().map(Token::type).map(Objects::toString).collect(Collectors.joining(" ")));

        System.out.println("Parser AST:\n" + runner.syntaxStatement().prettyString(0));

        System.out.println("Binder AST:\n" + runner.boundStatement());

        System.out.println("Lowered AST:\n" + runner.loweredBoundStatement());

        if (runner.diagnostics().hasErrors()) {
            runner.diagnostics().errors().forEach(System.err::println);
            Assertions.fail();
            return;
        }
    }
}
