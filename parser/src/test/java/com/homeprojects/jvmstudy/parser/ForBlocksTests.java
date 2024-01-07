package com.homeprojects.jvmstudy.parser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ForBlocksTests {

    private final Repl repl = new Repl();

    @Test
    public void forBlock() {
        String expression = """
                {
                    let a = 11;
                    {
                       for(let b = 0; b < 5; b = b + 1) {
                            a = a + 2;
                       }
                    }
                    a = a;
                }
                """;
        Object result = repl.evaluateToObject(expression);
        Assertions.assertThat(result).isEqualTo(21);
    }

    @Test
    public void forBlockBesides() {
        String expression = """
                {
                    let a = 11;
                    {
                       for(let b = 5; b < 10; b = b + 2) {
                            a = a + 2;
                       }
                       
                       for(let b = 15; b < 20; b = b + 1) {
                            a = a + 2;
                       }
                    }
                    a = a;
                }
                """;
        Object result = repl.evaluateToObject(expression);
        Assertions.assertThat(result).isEqualTo(27);
    }

    @Test
    public void forBlockNested() {
        String expression = """
                {
                    let a = 11;
                    {
                       for(let b = 5; b < 10; b = b + 1) {
                            a = a + 2;
                            for(let c = 15; c < 20; c = c + 1) {
                                a = a + 1;
                            }
                       }
                    }
                    a = a;
                }
                """;
        Object result = repl.evaluateToObject(expression);
        Assertions.assertThat(result).isEqualTo(46);
    }
}
