package come.homeproects.jvmstudy.parser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ForBlocksTests {

    private final Repl repl = new Repl();

    @Test
    public void forBlock() {
        String expression = """
                {
                    var a = 11;
                    {
                       for(var b = 0; b < 5; b = b + 1) {
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
                    var a = 11;
                    {
                       for(var b = 5; b < 10; b = b + 2) {
                            a = a + 2;
                       }
                       
                       for(var b = 15; b < 20; b = b + 1) {
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
                    var a = 11;
                    {
                       for(var b = 5; b < 10; b = b + 1) {
                            a = a + 2;
                            for(var b = 15; b < 20; b = b + 1) {
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
