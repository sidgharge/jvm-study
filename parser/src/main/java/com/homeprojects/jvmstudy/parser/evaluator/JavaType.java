package com.homeprojects.jvmstudy.parser.evaluator;

import com.homeprojects.jvmstudy.parser.types.Type;

import java.util.Arrays;

public enum JavaType {

    INT(int.class),
    STRING(String.class),
    BOOLEAN(boolean.class),
    VOID(void.class);

    private final Class clazz;

    JavaType(Class clazz) {
        this.clazz = clazz;
    }

    public Class clazz() {
        return clazz;
    }

    public JavaType findByType(Type type) {
        return Arrays.stream(values())
                .filter(m -> m.name().equals(type.name()))
                .findFirst()
                .get();
    }
}
