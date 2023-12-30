package com.homeprojects.jvmstudy.parser.lexer;

public record Token(String value, TokenType type, int startIndex, int endIndex, int lineNumber) {
}
