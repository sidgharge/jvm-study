package com.homeprojects.jvmstudy.parser.binder.statements;

import com.homeprojects.jvmstudy.parser.binder.expressions.BoundExpression;
import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.statements.BlockSyntaxStatement;
import com.homeprojects.jvmstudy.parser.statements.ParametersSyntax;
import com.homeprojects.jvmstudy.parser.types.Type;

public record MethodDeclarationBoundStatement(
        Token methodNameToken,
        Token openBracketToken,
        ParametersBound parametersBound,
        Token closedBracketToken,
        BlockBoundStatement methodBody,
        Token colonToken,
        Type returnType) implements BoundStatement {

    @Override
    public int startIndex() {
        return methodNameToken.startIndex();
    }

    @Override
    public int endIndex() {
        return methodNameToken.endIndex();
    }

    @Override
    public int lineNumber() {
        return methodNameToken.lineNumber();
    }

    @Override
    public String toString() {
        return prettyString(0);
    }

    @Override
    public String prettyString(int indent) {
        return String.format("%s%s%s%s%s%s %s\n%s",
                "  ".repeat(indent),
                methodNameToken.value(),
                openBracketToken.value(),
                parametersBound.toString(),
                closedBracketToken.value(),
                colonToken.value(),
                returnType.typeName(),
                methodBody.prettyString(indent));
    }
}
