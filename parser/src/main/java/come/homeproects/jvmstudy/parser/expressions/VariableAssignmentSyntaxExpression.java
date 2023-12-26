package come.homeproects.jvmstudy.parser.expressions;

import come.homeproects.jvmstudy.parser.lexer.Token;

import java.beans.Expression;

public record VariableAssignmentSyntaxExpression(Token identifierToken, Token equalsToken, Expression expression) implements SyntaxExpression {

    @Override
    public SyntaxExpressionType expressionType() {
        return SyntaxExpressionType.ASSIGNMENT_EXPRESSION;
    }
}
