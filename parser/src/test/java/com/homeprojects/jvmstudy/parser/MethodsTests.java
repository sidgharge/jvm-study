package com.homeprojects.jvmstudy.parser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MethodsTests {

    private final GlobalScopeRunner runner = new GlobalScopeRunner();

    @Test
    public void methodWithVariableInput() {
        Object result = runner.run("""
                sum(p: int, q: int): int {
                        let r: int = p + q;
                        return r;
                }
                let a = 10;
                sum(5, a);
                """);
        Assertions.assertThat(result).isEqualTo(15);
    }

    @Test
    public void methodWithVariableInputAndWithNumbersInName() {
        Object result = runner.run("""
                sum11(p: int, q: int): int {
                        let r: int = p + q;
                        return r;
                }
                let a = 10;
                sum11(5, a);
                """);
        Assertions.assertThat(result).isEqualTo(15);
    }

    @Test
    public void methodWithVariableInputAndWithNumbersInName2() {
        Object result = runner.run("""
                sum11a(p: int, q: int): int {
                        let r: int = p + q;
                        return r;
                }
                let a = 10;
                sum11a(5, a);
                """);
        Assertions.assertThat(result).isEqualTo(15);
    }

    @Test
    public void nestedMethodCalls() {
        Object result = runner.run("""
                sum(p: int, q: int): int {
                        let r: int = p + q;
                        return r;
                }
                sum(5, sum(15, 11));
                """);
        Assertions.assertThat(result).isEqualTo(31);
    }

    @Test
    public void continuousMethodCalls() {
        Object result = runner.run("""
                sum(p: int, q: int): int {
                        let r: int = p + q;
                        return r;
                }
                sum(15, 11);
                sum(5, 10);
                """);
        Assertions.assertThat(result).isEqualTo(15);
    }

    @Test
    public void methodCallInsideBlock() {
        Object result = runner.run("""
                sum(p: int, q: int): int {
                        let r: int = p + q;
                        return r;
                }
                {
                    sum(15, 11);
                }
                """);
        Assertions.assertThat(result).isEqualTo(26);
    }

    @Test
    public void methodCallBeforeMethodDeclared() {
        Object result = runner.run("""
                sum(15, 12);
                sum(p: int, q: int): int {
                        let r: int = p + q;
                        return r;
                }
                """);
        Assertions.assertThat(result).isEqualTo(27);
    }

    @Test
    public void methodCallBeforeMethodDeclaredInsideBlock() {
        Object result = runner.run("""
                {
                    sum(15, 12);
                }
                sum(p: int, q: int): int {
                        let r: int = p + q;
                        return r;
                }
                """);
        Assertions.assertThat(result).isEqualTo(27);
    }
}
