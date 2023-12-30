package com.homeprojects.jvmstudy.parser.binder.expressions;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record ArgumentsBound(List<ArgumentBoundExpression> expressions) {

    @Override
    public String toString() {
        return expressions.stream().map(Objects::toString).collect(Collectors.joining(" "));
    }
}
