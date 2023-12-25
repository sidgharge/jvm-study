package come.homeproects.jvmstudy.parser.lexer;

public enum TokenType {

    // Literals
    NUMBER_TOKEN,
    KEYWORD_TRUE_TOKEN,
    KEYWORD_FALSE_TOKEN,

    // Misc
    OPEN_BRACKET_TOKEN,
    CLOSED_BRACKET_TOKEN,
    END_OF_FILE_TOKEN,
    BAD_SYNTAX_TOKEN,

    // Operators
    PLUS_TOKEN,
    MINUS_TOKEN,
    START_TOKEN,
    SLASH_TOKEN,
    DOUBLE_AMPERSAND_TOKEN,
    DOUBLE_PIPE_TOKEN,
    DOUBLE_EQUALS_TOKEN,
    BANG_EQUALS_TOKEN,
    BANG_TOKEN,
}
