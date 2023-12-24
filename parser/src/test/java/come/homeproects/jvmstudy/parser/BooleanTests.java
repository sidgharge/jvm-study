package come.homeproects.jvmstudy.parser;

import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BooleanTests {

    private final CommandLineCalculator calculator = new CommandLineCalculator();

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
}