package come.homeproects.jvmstudy.parser;

import come.homeproects.jvmstudy.parser.bindexpressions.Binder;
import come.homeproects.jvmstudy.parser.bindexpressions.BoundExpression;
import come.homeproects.jvmstudy.parser.evaluator.Evaluator;
import come.homeproects.jvmstudy.parser.expressions.SyntaxExpression;

import java.util.Scanner;

public class CommandLineCalculator {

    public int evaluate(String expression) {
        return (int)evaluateToObject(expression);
    }

    public Object evaluateToObject(String expression) {
        Parser parser = new Parser(expression, false);
        SyntaxExpression exp = parser.parse();
        BoundExpression bind = new Binder().bind(exp);
        return new Evaluator().evaluate(bind);
    }

    public static void main(String[] args) {
        repl();
//        test();
    }

    public static void test() {
//        String expression = "4 - 1 + 5";

        String expression = """

                ab = 10
                """;
//        System.out.println(expression);

//        System.out.println(new Evaluator2().evaluate(new Parser(expression, true).parse()));

        Binder binder = new Binder();
        BoundExpression boundExpression = binder.bind(expression, true);
        if (binder.diagnostics().hasErrors()) {
            binder.diagnostics().errors().forEach(System.err::println);
//            System.out.println(new Evaluator().evaluate(boundExpression));
            return;
        }
        System.out.println(new Evaluator().evaluate(boundExpression));
    }

    public static void repl() {
        boolean debug = false;
        Binder binder = new Binder();
        Evaluator evaluator = new Evaluator();
        try(Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print(">> ");
                String line = scanner.nextLine();
                if (line.equals("q")) {
                    break;
                }
                if (line.equals("#debug")) {
                    debug = !debug;
                    System.out.println("debug = " + debug);
                    continue;
                }

                BoundExpression boundExpression = binder.bind(line, debug);
                if (binder.diagnostics().hasErrors()) {
                    binder.diagnostics().errors().forEach(System.err::println);
                    return;
                }
                System.out.println(evaluator.evaluate(boundExpression));
            }
        }
    }
}
