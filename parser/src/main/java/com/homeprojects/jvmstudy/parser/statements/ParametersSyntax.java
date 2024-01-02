package com.homeprojects.jvmstudy.parser.statements;

import java.util.List;
import java.util.stream.Collectors;

public record ParametersSyntax(List<ParameterSyntax> parameters) {

    @Override
    public String toString() {
        return parameters.stream()
                .map(Record::toString)
                .collect(Collectors.joining(" "));
    }
}
