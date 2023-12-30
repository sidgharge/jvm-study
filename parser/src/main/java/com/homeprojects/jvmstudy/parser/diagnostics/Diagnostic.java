package com.homeprojects.jvmstudy.parser.diagnostics;

public record Diagnostic(String message, int startIndex, int endIndex, int lineNumber) {

    @Override
    public String toString() {
        return String.format("%s at index %d on line %d", message, startIndex + 1, lineNumber + 1);
    }
}
