package com.homeprojects.jvmstudy.parser.diagnostics;

import com.homeprojects.jvmstudy.parser.lexer.Token;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Diagnostics {

    private final TreeSet<Diagnostic> errors;

    public Diagnostics() {
        errors = new TreeSet<>(
                Comparator.comparingInt(Diagnostic::lineNumber)
                        .thenComparingInt(Diagnostic::startIndex));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public Set<Diagnostic> errors() {
        return errors;
    }
    
    public void addDiagnostic(Token token, String error, Object... args) {
        this.errors.add(new Diagnostic(String.format(error, args), token.startIndex(), token.endIndex(), token.lineNumber()));;
    }

    public void addDiagnostic(int startIndex, int endIndex, int lineNumber, String error, Object... args) {
        this.errors.add(new Diagnostic(String.format(error, args), startIndex, endIndex, lineNumber));;
    }

    public void add(Diagnostic diagnostic) {
        this.errors.add(diagnostic);
    }
}
