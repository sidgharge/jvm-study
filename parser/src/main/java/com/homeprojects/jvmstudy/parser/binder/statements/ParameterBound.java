package com.homeprojects.jvmstudy.parser.binder.statements;

import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.types.Type;

public record ParameterBound(
        Token parameterNameToken,
        Token semiColonToken,
        Type type,
        Token commaToken) {

    @Override
    public String toString() {
        return String.format("%s%s %s%s",
                parameterNameToken.value(),
                semiColonToken.value(),
                type.typeName(),
                commaToken == null ? "" : commaToken.value());
    }
}
