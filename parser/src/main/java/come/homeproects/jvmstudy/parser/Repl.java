package come.homeproects.jvmstudy.parser;

import come.homeproects.jvmstudy.parser.binder.Binder;
import come.homeproects.jvmstudy.parser.binder.statements.BoundStatement;
import come.homeproects.jvmstudy.parser.diagnostics.Diagnostic;
import come.homeproects.jvmstudy.parser.evaluator.Evaluator;

import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Repl {

    public int evaluate(String expression) {
        return (int)evaluateToObject(expression);
    }

    public Object evaluateToObject(String expression) {
        return evaluateToObject(expression, false);
    }

    public Object evaluateToObject(String expression, boolean debug) {
        Binder binder = new Binder();
        BoundStatement boundStatement = binder.bind(expression);
        if (debug) {
            printDebugInfo(binder, boundStatement);
        }
        return new Evaluator().evaluate(boundStatement);
    }

    public static void main(String[] args) {
        repl();
//        test();
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
                    System.out.print("> ");
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

                binder.diagnostics().errors().clear();
                BoundStatement statement = binder.bind(line);

                if (debug) {
                    printDebugInfo(binder, statement);
                }

                if (binder.diagnostics().hasErrors()) {
                    binder.diagnostics().errors().forEach(Repl::printDiagnostic);
                } else {
                    System.out.println("=> " + evaluator.evaluate(statement));
                }
            }
        }
    }

    public static final String ANSI_RED = "\u001B[31m";

    public static final String ANSI_RESET = "\u001B[0m";

    private static void printDiagnostic(Diagnostic diagnostic) {
        String str = String.format("%s(%d, %d): %s%s", ANSI_RED, diagnostic.lineNumber() + 1, diagnostic.startIndex() + 1, diagnostic.message(), ANSI_RESET);
        System.out.println(str);
    }

    private static void printDebugInfo(Binder binder, BoundStatement statement) {
        System.out.println("Tokens:\n" + binder.tokens().stream().map(Objects::toString).collect(Collectors.joining(" ")));

        System.out.println("Parser AST:\n" + binder.syntaxStatement().printString(0));

        System.out.println("Binder AST:\n" + statement);
    }
}
