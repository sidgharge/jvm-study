package com.homeprojects.jvmstudy.parser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class UnaryOperationTests {

    private final Repl calculator = new Repl();

    @Test
    public void singleNumber1() {
        int evaluate = calculator.evaluate("-10");
        Assertions.assertThat(evaluate).isEqualTo(-10);
    }

    @Test
    public void singleNumber2() {
        int evaluate = calculator.evaluate("--10");
        Assertions.assertThat(evaluate).isEqualTo(10);
    }

    @Test
    public void singleNumberWithBracket() {
        int evaluate = calculator.evaluate("-(10)");
        Assertions.assertThat(evaluate).isEqualTo(-10);
    }

    @Test
    public void singleNumberWithBracket2() {
        int evaluate = calculator.evaluate("-(+10)");
        Assertions.assertThat(evaluate).isEqualTo(-10);
    }

    @Test
    public void singleNumberWithBracket3() {
        int evaluate = calculator.evaluate("--(+10)");
        Assertions.assertThat(evaluate).isEqualTo(10);
    }

    @Test
    public void operatorOnBracketExpression() {
        int evaluate = calculator.evaluate("-(10 - 25)");
        Assertions.assertThat(evaluate).isEqualTo(15);
    }

    @Test
    public void operatorOnBracketExpression2() {
        int evaluate = calculator.evaluate("-(-10 - 25)");
        Assertions.assertThat(evaluate).isEqualTo(35);
    }

    @Test
    public void operatorOnBracketExpression3() {
        int evaluate = calculator.evaluate("--(-10 +- 25)");
        Assertions.assertThat(evaluate).isEqualTo(-35);
    }

    @Test
    public void operatorOnBracketExpression4() {
        int evaluate = calculator.evaluate("-(-10 - 25 + 30)");
        Assertions.assertThat(evaluate).isEqualTo(5);
    }
}
