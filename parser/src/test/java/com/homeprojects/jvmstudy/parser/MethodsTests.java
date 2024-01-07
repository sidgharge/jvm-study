package com.homeprojects.jvmstudy.parser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MethodsTests {

    private final Repl repl = new Repl();

    @Test
    public void methodWithVariableInput() {
        Object result = repl.evaluateToObject("""
                {
                    let a = 10;
                    
                    sum(p: int, q: int): int {
                        let r: int = p + q;
                        return r;
                    }
                    
                    sum(5, a);
                }
                """);
        Assertions.assertThat(result).isEqualTo(15);
    }

    @Test
    public void nestedMethodCalls() {
        Object result = repl.evaluateToObject("""
                {
                    sum(p: int, q: int): int {
                        let r: int = p + q;
                        return r;
                    }
                    
                    sum(5, sum(15, 11));
                }
                """);
        Assertions.assertThat(result).isEqualTo(31);
    }

    @Test
    public void continuousMethodCalls() {
        Object result = repl.evaluateToObject("""
                {
                    sum(p: int, q: int): int {
                        let r: int = p + q;
                        return r;
                    }
                    
                    sum(15, 11);
                    sum(5, 10);
                }
                """);
        Assertions.assertThat(result).isEqualTo(15);
    }
}
