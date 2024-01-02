package com.homeprojects.jvmstudy.parser.statements;

import com.homeprojects.jvmstudy.parser.lexer.Token;

public record ParameterSyntax(Token parameterNameToken,
                              Token semiColonToken,
                              Token typeToken,
                              Token commaToken) {

    @Override
    public String toString() {
        return String.format("%s%s %s%s",
                parameterNameToken.value(),
                semiColonToken.value(),
                typeToken.value(),
                commaToken == null ? "" : commaToken.value());
    }
}
