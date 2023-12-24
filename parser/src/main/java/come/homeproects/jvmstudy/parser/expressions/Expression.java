package come.homeproects.jvmstudy.parser.expressions;

import come.homeproects.jvmstudy.parser.lexer.Token;

import java.util.List;

public interface Expression {

    List<Expression> children();

    ExpressionType expressionType();

    Token token();

}
