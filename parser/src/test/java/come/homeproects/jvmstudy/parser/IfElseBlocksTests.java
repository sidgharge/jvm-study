package come.homeproects.jvmstudy.parser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class IfElseBlocksTests {

    private final Repl repl = new Repl();

    @Test
    public void ifBlockTest() {
        String expression = """
                {
                    let a = 10;
                    {
                        if(a < 15 && a > 6) {
                            a = 20;
                        }
                    }
                    a = a;
                }
                """;
        Object result = repl.evaluateToObject(expression);
        Assertions.assertThat(result).isEqualTo(20);
    }

    @Test
    public void nestedIfBlockTest() {
        String expression = """
                {
                    let a = 10;
                    {
                        if(a < 15 && a > 6) {
                            a = 20;
                            if(a > 11) {
                                a = 30;
                            }
                            if(a > 21) {
                                a = 40;
                            }
                            if(a > 41) {
                                a = 50;
                            }
                        }
                    }
                    a = a;
                }
                """;
        Object result = repl.evaluateToObject(expression);
        Assertions.assertThat(result).isEqualTo(40);
    }

    @Test
    public void ifElseBlock1() {
        String expression = """
                {
                    let a = 10;
                    {
                        if(a < 15 && a > 6) {
                            a = 20;
                        } else {
                            a = 5;
                        }
                    }
                    a = a;
                }
                """;
        Object result = repl.evaluateToObject(expression);
        Assertions.assertThat(result).isEqualTo(20);
    }

    @Test
    public void ifElseBlock2() {
        String expression = """
                {
                    let a = 1;
                    {
                        if(a < 15 && a > 6) {
                            a = 20;
                        } else {
                            a = 5;
                        }
                    }
                    a = a;
                }
                """;
        Object result = repl.evaluateToObject(expression);
        Assertions.assertThat(result).isEqualTo(5);
    }

    @Test
    public void nestedIfElseBlock2() {
        String expression = """
                {
                    let a = 1;
                    {
                        if(a < 15 && a > 6) {
                            a = 20;
                        } else {
                            if(a == 1) {
                                a= 100;
                            } else {
                                a = 50;
                            }
                        }
                    }
                    a = a;
                }
                """;
        Object result = repl.evaluateToObject(expression);
        Assertions.assertThat(result).isEqualTo(100);
    }
}
