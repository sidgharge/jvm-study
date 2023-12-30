package com.homeprojects.jvmstudy.parser.binder.expressions;

import com.homeprojects.jvmstudy.parser.types.Type;

public interface BoundExpression {

    Type type();

    record NoOpBoundExpression(String dummyValue) implements BoundExpression {

        @Override
        public Type type() {
            return Type.UNKNOWN;
        }

        @Override
        public String toString() {
            return Type.UNKNOWN.name();
        }
    }
}
