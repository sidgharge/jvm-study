package com.homeprojects.jvmstudy.parser.types;

import java.util.Arrays;

public enum Type {

    INT("int"), BOOLEAN("boolean"), UNKNOWN("unknown");

    private final String typeName;

    Type(String typeName) {
        this.typeName = typeName;
    }

    public String typeName() {
        return typeName;
    }

    @Override
    public String toString() {
        return typeName;
    }

    public static Type fromName(String typeName) {
        return Arrays.stream(values())
                .filter(t -> t.typeName.equals(typeName))
                .findFirst()
                .orElse(null);
    }

}
