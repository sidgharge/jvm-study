package com.homeprojects.jvmstudy.parser.expressions;

import java.util.List;
import java.util.stream.Collectors;

public record ArgumentsSyntax(List<ArgumentSyntax> argumentSyntaxes) {

    @Override
    public String toString() {
        return this.argumentSyntaxes.stream().map(Record::toString).collect(Collectors.joining(" "));
    }
}
