package come.homeproects.jvmstudy.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class CommandLineCalculatorTests {

    private final CommandLineCalculator calculator = new CommandLineCalculator();

    @Test
    public void returnNumberWhenNumberIsPassed1() {
        int result = calculator.evaluate("5");
        assertThat(result).isEqualTo(5);
    }

    @Test
    public void returnNumberWhenNumberIsPassed2() {
        int result = calculator.evaluate("257");
        assertThat(result).isEqualTo(257);
    }

    @Test
    public void simpleAddition() {
        int result = calculator.evaluate("27+12");
        assertThat(result).isEqualTo(39);
    }

    @Test
    public void simpleSubtraction() {
        int result = calculator.evaluate("27-12");
        assertThat(result).isEqualTo(15);
    }

    @Test
    public void simpleMultiplication() {
        int result = calculator.evaluate("12*10");
        assertThat(result).isEqualTo(120);
    }

    @Test
    public void simpleDivision() {
        int result = calculator.evaluate("120/12");
        assertThat(result).isEqualTo(10);
    }

    @Test
    public void acceptSpaces() {
        int result = calculator.evaluate("12 + 5");
        assertThat(result).isEqualTo(17);
    }

    @Test
    public void multiOperatorAdditionSubtraction() {
        int result = calculator.evaluate("12 - 5 - 2");
        assertThat(result).isEqualTo(5);
    }


    @Test
    public void multiOperatorAdditionSubtraction2() {
        int result = calculator.evaluate("12 - 5 + 2");
        assertThat(result).isEqualTo(9);
    }

    @Test
    public void multiOperatorAdditionSubtraction3() {
        int result = calculator.evaluate("12 - 5 + 2 - 3 + 4");
        assertThat(result).isEqualTo(10);
    }

    @Test
    public void multiOperatorMultiplicationDivision() {
        int result = calculator.evaluate("12 * 5 / 2");
        assertThat(result).isEqualTo(30);
    }
}