package com.homeprojects.jvmstudy.parser.binder.statements;

import java.util.List;
import java.util.stream.Collectors;

public record ParametersBound(List<ParameterBound> parameters) {

    @Override
    public String toString() {
        return parameters.stream()
                .map(ParameterBound::toString)
                .collect(Collectors.joining(" "));
    }
}
