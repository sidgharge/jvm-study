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
        String expression = """
                let a = 10;
                """;

        String expression1 = """
                {
                    let a = 11;
                    {
                       for(let b = 5; b < 10; b = b + 1) {
                            a = a + 2;
                            for(let c = 15; c < 20; c = c + 1) {
                                a = a + 1;
                            }
                       }
                    }
                    {
                        let b = 10;
                        a = a + 10;
                    }
                    println("is a = 56 ?", a);
                }
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
