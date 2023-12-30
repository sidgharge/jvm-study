package come.homeproects.jvmstudy.parser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class BlocksTests {

    private final Repl repl = new Repl();

    @Test
    public void blockWithNumbers() {
        Object result = repl.evaluateToObject("""
                {
                    let a = 10;
                    {
                        let b = a + 20;
                        b = b;
                    }
                }
                """);
        Assertions.assertThat(result).isEqualTo(30);
    }

    @Test
    public void blockWithBooleans() {
        Object result = repl.evaluateToObject("""
                {
                    let a = 10;
                    {
                        let b = a == 10;
                        b = b;
                    }
                }
                """);
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    public void block1() {
        Object result = repl.evaluateToObject("""
                let x = 5;
                {
                    let a = 10;
                    {
                        let b = x + 10 + a;
                        b = b;
                    }
                }
                """);
        Assertions.assertThat(result).isEqualTo(25);
    }

    @Test
    public void block2() {
        String expression = """
                {
                    let a = 10;
                    {
                        let b = 10;
                        let a = a * 20 + b;
                    }
                    a = a;
                }
                """;
        Object result = repl.evaluateToObject(expression);
        Assertions.assertThat(result).isEqualTo(10);
    }

    @Test
    public void varAssignmentWithType() {
        String expression = """
                {
                    let a: int = 10;
                    a = a;
                }
                """;
        Object result = repl.evaluateToObject(expression);
        Assertions.assertThat(result).isEqualTo(10);
    }

    @Test
    public void varReassignment() {
        String expression = """
                {
                    let a = 10;
                    {
                        a = 20;
                        let b = a * 3;
                    }
                }
                """;
        Object result = repl.evaluateToObject(expression);
        Assertions.assertThat(result).isEqualTo(60);
    }
}
