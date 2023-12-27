package come.homeproects.jvmstudy.parser.binder.expressions;

import java.lang.reflect.Type;

public interface BoundExpression {

    Type type();

    record NoOpBoundExpression(String dummyValue) implements BoundExpression {

        @Override
        public Type type() {
            return Object.class;
        }

        @Override
        public String toString() {
            return dummyValue;
        }
    }
}
