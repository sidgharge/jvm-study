package com.homeprojects.jvmstudy.parser;

import com.homeprojects.jvmstudy.parser.binder.Binder;
import com.homeprojects.jvmstudy.parser.binder.statements.BoundStatement;
import com.homeprojects.jvmstudy.parser.evaluator.Evaluator;
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
        String expression1 = "var a = 5; a = a + 10";

        String expression = """
                {
                    println("Enter your name:");
                    let name = input();
                    println("Hello", name);
                }
                """;

        Binder binder = new Binder();
        BoundStatement statement = binder.bind(expression);

        System.out.println("Tokens:\n" + binder.tokens().stream().map(Token::type).map(Objects::toString).collect(Collectors.joining(" ")));

        System.out.println("Parser AST:\n" + binder.syntaxStatement().prettyString(0));

        System.out.println("Binder AST:\n" + statement);

        if (binder.diagnostics().hasErrors()) {
            binder.diagnostics().errors().forEach(System.err::println);
            Assertions.fail();
            return;
        }
        System.out.println("--------------------------------------");
//        System.out.println("result: " + new Evaluator().evaluate(statement));
        new Evaluator().evaluate(statement);
    }
}
