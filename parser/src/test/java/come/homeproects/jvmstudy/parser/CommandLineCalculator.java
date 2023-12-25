package come.homeproects.jvmstudy.parser;

import come.homeproects.jvmstudy.parser.bindexpressions.Binder;
import come.homeproects.jvmstudy.parser.bindexpressions.BoundExpression;
import come.homeproects.jvmstudy.parser.evaluator.Evaluator;
import come.homeproects.jvmstudy.parser.evaluator.Evaluator2;
import come.homeproects.jvmstudy.parser.expressions.Expression;

public class CommandLineCalculator {

    public int evaluate(String expression) {
        return (int)evaluateToObject(expression);
    }

    public Object evaluateToObject(String expression) {
        Parser parser = new Parser(expression, false);
        Expression exp = parser.parse();
        BoundExpression bind = new Binder().bind(exp);
        return new Evaluator().evaluate(bind);
    }

    public static void main(String[] args) {
//        String expression = "4 - 1 + 5";

        String expression = "1 + 5 * 6";

//        System.out.println(new Evaluator2().evaluate(new Parser(expression, true).parse()));

        Binder binder = new Binder();
        BoundExpression boundExpression = binder.bind(expression);
        if (binder.diagnostics().hasErrors()) {
            binder.diagnostics().errors().forEach(System.err::println);
            System.out.println(new Evaluator().evaluate(boundExpression));
            return;
        }
        System.out.println(new Evaluator().evaluate(boundExpression));
    }
}
