package come.homeproects.jvmstudy.parser;

import come.homeproects.jvmstudy.parser.binder.Binder;
import come.homeproects.jvmstudy.parser.binder.expressions.BoundExpression;
import come.homeproects.jvmstudy.parser.binder.statements.BoundStatement;
import come.homeproects.jvmstudy.parser.evaluator.Evaluator;
import come.homeproects.jvmstudy.parser.expressions.SyntaxExpression;

import java.util.Scanner;

public class CommandLineCalculator {

    public int evaluate(String expression) {
        return (int)evaluateToObject(expression);
    }

    public Object evaluateToObject(String expression) {
        BoundStatement boundStatement = new Binder().bind(expression, false);
        return new Evaluator().evaluate(boundStatement);
    }

    public static void main(String[] args) {
//        repl();
        test();
    }

    public static void test() {
//        String expression = "2 *(3 + 4 * (5 + 6))";

        String expression = """
                {
                    a = 10
                    b = 12
                }
                """;
//        System.out.println(expression);

//        System.out.println(new Evaluator2().evaluate(new Parser(expression, true).parse()));

        Binder binder = new Binder();
        BoundStatement statement = binder.bind(expression, true);
        if (binder.diagnostics().hasErrors()) {
            binder.diagnostics().errors().forEach(System.err::println);
//            System.out.println(new Evaluator().evaluate(boundExpression));
            return;
        }
        System.out.println(new Evaluator().evaluate(statement));
    }

    public static void repl() {
        boolean debug = false;
        Binder binder = new Binder();
        Evaluator evaluator = new Evaluator();
        StringBuilder builder = new StringBuilder();
        try(Scanner scanner = new Scanner(System.in)) {
            while (true) {
                if (builder.isEmpty()) {
                    System.out.print(">> ");
                } else {
//                    System.out.print("| ");
                }
                String line = scanner.nextLine();
                if (line.equals("q")) {
                    break;
                }
                if (line.equals("#debug")) {
                    debug = !debug;
                    System.out.println("debug = " + debug);
                    continue;
                }
                if (!line.isEmpty()) {
                    builder.append(line);
                    continue;
                }

                line = builder.toString();
                builder = new StringBuilder();

                BoundStatement statement = binder.bind(line, debug);
                if (binder.diagnostics().hasErrors()) {
                    binder.diagnostics().errors().forEach(System.err::println);

                } else {
                    System.out.println("=> " + evaluator.evaluate(statement));
                }
            }
        }
    }
}
