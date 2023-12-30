package com.homeprojects.jvmstudy.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class BooleanTests {

    private final Repl calculator = new Repl();

    @Test
    public void singleBoolean1() {
        Object result = calculator.evaluateToObject("true");
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void singleBoolean2() {
        Object result = calculator.evaluateToObject("false");
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void twoBooleans1() {
        Object result = calculator.evaluateToObject("false && true");
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void twoBooleans2() {
        Object result = calculator.evaluateToObject("true || false");
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void brackets1() {
        Object result = calculator.evaluateToObject("(false && true) || true");
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void brackets2() {
        Object result = calculator.evaluateToObject("(true || false) && false");
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void doubleBrackets1() {
        Object result = calculator.evaluateToObject("(false && true) || (true && true)");
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void doubleBrackets2() {
        Object result = calculator.evaluateToObject("(true || false) && (false && true)");
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void numbersEquality() {
        Object result = calculator.evaluateToObject("1 + 2 == 3");
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void numbersEquality2() {
        Object result = calculator.evaluateToObject("1 + 4 == 3");
        assertThat(result).isEqualTo(false);
    }


    @Test
    public void numbersEquality3() {
        Object result = calculator.evaluateToObject("1 + 4 == 3 + 2");
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void numbersEquality4() {
        Object result = calculator.evaluateToObject("1 + 4 == 3 + 5 == false");
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void notEquals1() {
        Object result = calculator.evaluateToObject("true != false");
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void notEquals2() {
        Object result = calculator.evaluateToObject("1 + 2 !=  2 + 3");
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void notEquals3() {
        Object result = calculator.evaluateToObject("1 + 2 !=  3");
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void negation1() {
        Object result = calculator.evaluateToObject("!true");
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void negation2() {
        Object result = calculator.evaluateToObject("(true && false) == !true");
        assertThat(result).isEqualTo(true);
    }

    @Test
    public void negation3() {
        Object result = calculator.evaluateToObject("!(true && false)");
        assertThat(result).isEqualTo(true);
    }

    @ParameterizedTest
    @MethodSource
    public void comparingOperatorsTests(String expression, Object expectedValue) {
        Object result = calculator.evaluateToObject(expression);
        assertThat(result).isEqualTo(expectedValue);
    }

    private static Stream<Arguments> comparingOperatorsTests() {
        return Stream.of(
                arguments("10 > 20", false),
                arguments("10 > 5", true),
                arguments("10 < 20", true),
                arguments("10 < 5", false),
                arguments("10 >= 20", false),
                arguments("10 <= 20", true),
                arguments("10 <= 10", true),
                arguments("10 >= 10", true),
                arguments("10 >= 10 && 10 < 5", false),
                arguments("10 < 10 || 10 >= 6", true),
                arguments("10 >= 10 && (5 < 4 || 10 > 4)", true),
                arguments("10 < 10 || 5 < 4 ", false)
        );
    }

}
