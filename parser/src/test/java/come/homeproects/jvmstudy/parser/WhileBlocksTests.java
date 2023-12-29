package come.homeproects.jvmstudy.parser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class WhileBlocksTests {

    private final Repl repl = new Repl();

    @Test
    public void whileBlock() {
        String expression = """
                {
                    var a = 11;
                    {
                       while(a < 20) {
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
    public void whileBlockBesided() {
        String expression = """
                {
                    var a = 11;
                    {
                       while(a < 20) {
                            a = a + 2;
                       }
                       
                       while(a < 30) {
                            a = a + 4;
                       }
                    }
                    a = a;
                }
                """;
        Object result = repl.evaluateToObject(expression);
        Assertions.assertThat(result).isEqualTo(33);
    }

    @Test
    public void whileBlockNested() {
        String expression = """
                {
                    var a = 11;
                    {
                       while(a < 15) {
                            a = a + 2;
                            var b = 1;
                            while(b < 4) {
                                b = b + 1;
                                a = a + 1;
                            }
                       }
                    }
                    a = a;
                }
                """;
        Object result = repl.evaluateToObject(expression);
        Assertions.assertThat(result).isEqualTo(16);
    }
}
