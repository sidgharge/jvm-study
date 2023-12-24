package come.homeproects.jvmstudy.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class BracketsTests {

    private final CommandLineCalculator calculator = new CommandLineCalculator();

    @Test
    public void bracket1() {
        int result = calculator.evaluate("12 - (5 + 3)");
        assertThat(result).isEqualTo(4);
    }

    @Test
    public void bracket2() {
        int result = calculator.evaluate("12 * (5 + 3)");
        assertThat(result).isEqualTo(96);
    }

    @Test
    public void bracket3() {
        int result = calculator.evaluate("(5 + 3) * 7");
        assertThat(result).isEqualTo(56);
    }

    @Test
    public void bracket4() {
        int result = calculator.evaluate("(5 + 3) * 7 - (2 + 4)");
        assertThat(result).isEqualTo(50);
    }

    @Test
    public void bracket5() {
        int result = calculator.evaluate("(15 - (3 - 1))");
        assertThat(result).isEqualTo(13);
    }

    @Test
    public void bracket6() {
        int result = calculator.evaluate("((15 - 3) - 1))");
        assertThat(result).isEqualTo(11);
    }
}