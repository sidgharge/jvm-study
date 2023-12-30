package com.homeprojects.jvmstudy.parser.diagnostics;

import com.homeprojects.jvmstudy.parser.lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class Diagnostics {

    private final List<Diagnostic> errors;

    public Diagnostics() {
        errors = new ArrayList<>();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<Diagnostic> errors() {
        return errors;
    }

//    public void addDiagnostic(String error, Object... args) {
//        errors.add(String.format(error, args));
//    }
    public void addDiagnostic(Token token, String error, Object... args) {
        this.errors.add(new Diagnostic(String.format(error, args), token.startIndex(), token.endIndex(), token.lineNumber()));;
    }

    public void addDiagnostic(String error, Object... args) {
        this.errors.add(new Diagnostic(String.format(error, args), 0, 0, 0));;
    }

    public void add(Diagnostic diagnostic) {
        this.errors.add(diagnostic);
    }
}
