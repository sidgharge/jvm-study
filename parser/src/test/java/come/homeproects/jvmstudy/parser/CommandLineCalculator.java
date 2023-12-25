package come.homeproects.jvmstudy.parser;

import come.homeproects.jvmstudy.parser.evaluator.Evaluator;
import come.homeproects.jvmstudy.parser.expressions.Expression;

import java.util.Scanner;

public class CommandLineCalculator {

    public int evaluate(String expression) {
        return (int)evaluateToObject(expression);
    }

    public Object evaluateToObject(String expression) {
        Parser parser = new Parser(expression, false);
        Expression exp = parser.parse();

//        System.out.println("--------------- Expression -------------------------");
//        System.out.println(exp);

        return new Evaluator().evaluate(exp);
    }

    public static void main(String[] args) {
//        String expression = "4 - 1 + 5";
        String expression = "4 + 2 == 5 + 1 == true";

        Expression exp = new Parser(expression,  true).parse();
        System.out.println("--------------- Expression -------------------------");
        System.out.print(exp + " = ");
        System.out.println(new Evaluator().evaluate(exp));
    }

    public static void main2(String[] args) {
        try(Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String line = scanner.nextLine();
                if (line.equals("q")) {
                    break;
                }
                Expression expression = new Parser(line, false).parse();
                System.out.println("Answer: " + new Evaluator().evaluate(expression));
            }
        }


    }
}
