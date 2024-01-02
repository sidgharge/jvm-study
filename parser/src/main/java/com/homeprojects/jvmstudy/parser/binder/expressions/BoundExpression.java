package com.homeprojects.jvmstudy.parser.binder.expressions;

import com.homeprojects.jvmstudy.parser.types.Type;

public interface BoundExpression {

    Type type();

    int startIndex();

    int endIndex();

    int lineNumber();

    record NoOpBoundExpression(String dummyValue, int startIndex, int endIndex, int lineNumber) implements BoundExpression {

        @Override
        public Type type() {
            return Type.UNKNOWN;
        }

        @Override
        public String toString() {
            return Type.UNKNOWN.typeName();
        }
    }
}
