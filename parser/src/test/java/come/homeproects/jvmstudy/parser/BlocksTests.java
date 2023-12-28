package come.homeproects.jvmstudy.parser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class BlocksTests {

    private final Repl repl = new Repl();

    @Test
    public void blockWithNumbers() {
        Object result = repl.evaluateToObject("""
                {
                    var a = 10;
                    {
                        var b = a + 20;
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
                    var a = 10;
                    {
                        var b = a == 10;
                        b = b;
                    }
                }
                """);
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    public void block1() {
        Object result = repl.evaluateToObject("""
                var x = 5;
                {
                    var a = 10;
                    {
                        var b = x + 10 + a;
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
                    var a = 10;
                    {
                        var b = 10;
                        var a = a * 20 + b;
                    }
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
                    var a = 10;
                    {
                        a = 20;
                        var b = a * 3;
                    }
                }
                """;
        Object result = repl.evaluateToObject(expression);
        Assertions.assertThat(result).isEqualTo(60);
    }
}
