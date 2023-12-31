package com.homeprojects.jvmstudy.parser.lexer;

import java.util.Set;

public enum TokenType {

    // Literals
    NUMBER_TOKEN,
    KEYWORD_TRUE_TOKEN,
    KEYWORD_FALSE_TOKEN,
    STRING_TOKEN,

    // Misc
    OPEN_BRACKET_TOKEN,
    CLOSED_BRACKET_TOKEN,
    END_OF_FILE_TOKEN,
    BAD_SYNTAX_TOKEN,
    IDENTIFIER_TOKEN,

    // Operators
    PLUS_TOKEN,
    MINUS_TOKEN,
    START_TOKEN,
    SLASH_TOKEN,
    DOUBLE_AMPERSAND_TOKEN,
    DOUBLE_PIPE_TOKEN,
    DOUBLE_EQUALS_TOKEN,
    BANG_EQUALS_TOKEN,
    GREATER_THAN_TOKEN,
    GREATER_THAN_EQUALS_TOKEN,
    LESS_THAN_TOKEN,
    LESS_THAN_EQUALS_TOKEN,
    BANG_TOKEN,
    EQUALS_TOKEN,

    // Statement
    OPEN_CURLY_BRACKET_TOKEN,
    CLOSED_CURLY_BRACKET_TOKEN,
    SEMI_COLON_TOKEN,
    COLON_TOKEN,
    COMMA_TOKEN,
    KEYWORD_LET_TOKEN,
    KEYWORD_IF_TOKEN,
    KEYWORD_ELSE_TOKEN,
    KEYWORD_WHILE_TOKEN,
    KEYWORD_FOR_TOKEN,
    KEYWORD_RETURN_TOKEN;

    public boolean isMathematicalOperatorToken() {
        return this == PLUS_TOKEN ||
                this == MINUS_TOKEN ||
                this == START_TOKEN ||
                this == SLASH_TOKEN;
    }

    private static final Set<TokenType> LOGICAL_OPERATORS = Set.of(
            DOUBLE_EQUALS_TOKEN,
            BANG_EQUALS_TOKEN,
            GREATER_THAN_TOKEN,
            GREATER_THAN_EQUALS_TOKEN,
            LESS_THAN_TOKEN,
            LESS_THAN_EQUALS_TOKEN);

    public boolean isLogicalOperatorToken() {
        return LOGICAL_OPERATORS.contains(this);
    }

    public boolean isOnlyBooleanLogicalOperator() {
        return this == DOUBLE_PIPE_TOKEN || this == DOUBLE_AMPERSAND_TOKEN;
    }
}
