package com.homeprojects.jvmstudy.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class BodmasTests {

    private final Repl calculator = new Repl();

    @Test
    public void additionThenMultiplication() {
        int result = calculator.evaluate("2 + 3 * 4");
        assertThat(result).isEqualTo(14);
    }

    @Test
    public void multiplications() {
        int result = calculator.evaluate("2 * 3 * 4");
        assertThat(result).isEqualTo(24);
    }

    @Test
    public void multiplicationThenAddition() {
        int result = calculator.evaluate("2 * 3 + 4");
        assertThat(result).isEqualTo(10);
    }

    @Test
    public void multiplicationThenSubtraction() {
        int result = calculator.evaluate("2 * 3 - 4");
        assertThat(result).isEqualTo(2);
    }

    @Test
    public void divisions() {
        int result = calculator.evaluate("50 / 2 / 5");
        assertThat(result).isEqualTo(5);
    }

    @Test
    public void divisionThenAddition() {
        int result = calculator.evaluate("10 / 2 + 4");
        assertThat(result).isEqualTo(9);
    }

    @Test
    public void DivisionThenSubtraction() {
        int result = calculator.evaluate("10 / 2 - 4");
        assertThat(result).isEqualTo(1);
    }

    @Test
    public void DivisionThenSubtraction2() {
        int result = calculator.evaluate("10 / 2 - 4 * 2");
        assertThat(result).isEqualTo(-3);
    }
}