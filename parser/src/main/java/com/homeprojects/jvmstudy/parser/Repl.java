package com.homeprojects.jvmstudy.parser;

import com.homeprojects.jvmstudy.parser.binder.Binder;
import com.homeprojects.jvmstudy.parser.binder.statements.BoundStatement;
import com.homeprojects.jvmstudy.parser.diagnostics.Diagnostic;
import com.homeprojects.jvmstudy.parser.evaluator.Evaluator;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Repl {

    public int evaluate(String expression) {
        return (int)evaluateToObject(expression);
    }

    public Object evaluateToObject(String expression) {
        return new Runner(expression).run();
    }

    public static void main(String[] args) {
        repl();
//        test();
    }

    public static void repl() {
        boolean debug = false;
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
                if (line.equals("debug")) {
                    debug = !debug;
                    System.out.println("debug = " + debug);
                    continue;
                }
                if (line.equals("cls") || line.equals("clear")) {
                    try {
                        new ProcessBuilder(line).inheritIO().start().exitValue();
                        builder = new StringBuilder();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                builder.append(line);
                if (line.isEmpty() || line.lastIndexOf('\'') != line.length() - 1) {
                    continue;
                }

                builder.deleteCharAt(builder.length() - 1);
                line = builder.toString();

                builder = new StringBuilder();

                Runner runner = new Runner(line);
                Object result = runner.run(false);

                if (debug) {
                    printDebugInfo(runner);
                }

                if (runner.diagnostics().hasErrors()) {
                    runner.diagnostics().errors().forEach(Repl::printDiagnostic);
                } else {
                    System.out.println("=> " + result);
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

    private static void printDebugInfo(Runner runner) {
        System.out.println("Tokens:\n" + runner.tokens().stream()
                .map(token -> String.format("'%s': %s", token.value(), token.type()))
                .collect(Collectors.joining(", ")));

        System.out.println("Parser AST:\n" + runner.syntaxStatement().prettyString(0));

        System.out.println("Binder AST:\n" + runner.boundStatement());

        System.out.println("Lowered Statements:\n" + runner.boundStatement());
    }
}
