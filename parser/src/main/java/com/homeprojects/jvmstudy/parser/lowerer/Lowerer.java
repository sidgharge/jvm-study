package com.homeprojects.jvmstudy.parser.lowerer;

import com.homeprojects.jvmstudy.parser.binder.statements.BlockBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.BoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.ForBlockBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.IfBlockBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.MethodDeclarationBoundStatement;
import com.homeprojects.jvmstudy.parser.binder.statements.WhileBlockBoundStatement;
import com.homeprojects.jvmstudy.parser.lexer.Token;
import com.homeprojects.jvmstudy.parser.lexer.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Lowerer {

    private final BoundStatement parent;

    private int labelCounter;

    public Lowerer(BoundStatement parent) {
        this.parent = parent;
        this.labelCounter = 0;
    }

    public BoundStatement lower() {
        List<BoundStatement> statements = boundStatement(parent);
        return new BlockBoundStatement(
                new Token("{", TokenType.OPEN_CURLY_BRACKET_TOKEN, 0, 0, 0),
                new Token("}", TokenType.CLOSED_CURLY_BRACKET_TOKEN, 0 , 0, 0),
                statements
        );
    }

    private List<BoundStatement> boundStatement(BoundStatement boundStatement) {
        return switch (boundStatement) {
            case BlockBoundStatement blockBoundStatement -> blockBoundStatement(blockBoundStatement);
            case IfBlockBoundStatement ifBlockBoundStatement -> ifBlockBoundStatement(ifBlockBoundStatement);
            case WhileBlockBoundStatement whileBlockBoundStatement -> whileBlockBoundStatement(whileBlockBoundStatement);
            case ForBlockBoundStatement forBlockBoundStatement -> forBlockBoundStatement(forBlockBoundStatement);
            case MethodDeclarationBoundStatement methodDeclarationBoundStatement -> methodDeclarationBoundStatement(methodDeclarationBoundStatement);
            default -> Arrays.asList(boundStatement);
        };
    }

    private List<BoundStatement> methodDeclarationBoundStatement(MethodDeclarationBoundStatement methodDeclarationBoundStatement) {
        List<BoundStatement> statements = blockBoundStatement(methodDeclarationBoundStatement.methodBody());
        BlockBoundStatement blockBoundStatement = new BlockBoundStatement(
                methodDeclarationBoundStatement.methodBody().openBrace(),
                methodDeclarationBoundStatement.methodBody().closedBrace(),
                statements
        );

        MethodDeclarationBoundStatement updatedMethodDeclarationBoundStatement = new MethodDeclarationBoundStatement(
                methodDeclarationBoundStatement.methodNameToken(),
                methodDeclarationBoundStatement.openBracketToken(),
                methodDeclarationBoundStatement.parametersBound(),
                methodDeclarationBoundStatement.closedBracketToken(),
                blockBoundStatement,
                methodDeclarationBoundStatement.colonToken(),
                methodDeclarationBoundStatement.returnType()
        );

        return Arrays.asList(updatedMethodDeclarationBoundStatement);
    }

    private List<BoundStatement> blockBoundStatement(BlockBoundStatement blockBoundStatement) {
        return blockBoundStatement.statements()
                .stream()
                .flatMap(statement -> boundStatement(statement).stream())
                .collect(Collectors.toList());
    }

    private List<BoundStatement> ifBlockBoundStatement(IfBlockBoundStatement ifStatement) {
        List<BoundStatement> statements = new ArrayList<>();

        Label endLabel = newLabel();
        Label elseLabel = ifStatement.elseBlockBody().isPresent() ? newLabel() : endLabel;
        LabelBoundStatement endStatement = new LabelBoundStatement(endLabel);
        ConditionalGotoBoundStatement conditionalGotoBoundStatement = new ConditionalGotoBoundStatement(ifStatement.condition(), elseLabel);

        statements.add(conditionalGotoBoundStatement);
        statements.addAll(blockBoundStatement(ifStatement.ifBlockBody()));

        if (ifStatement.elseBlockBody().isPresent()) {
            statements.add(new GotoBoundStatement(endLabel));
            statements.add(new LabelBoundStatement(elseLabel));
            statements.addAll(blockBoundStatement(ifStatement.elseBlockBody().get().elseBlockBody()));
        }
        statements.add(endStatement);

        return statements;
    }

    private List<BoundStatement> whileBlockBoundStatement(WhileBlockBoundStatement whileStatement) {
        List<BoundStatement> statements = new ArrayList<>();

        Label startLabel = newLabel();
        Label endLabel = newLabel();

        statements.add(new LabelBoundStatement(startLabel));

        statements.add(new ConditionalGotoBoundStatement(whileStatement.condition(), endLabel));

        List<BoundStatement> whileBodyStatements = blockBoundStatement(whileStatement.whileBlockBody());
        statements.addAll(whileBodyStatements);

        statements.add(new GotoBoundStatement(startLabel));

        statements.add(new LabelBoundStatement(endLabel));

        return statements;
    }

    private List<BoundStatement> forBlockBoundStatement(ForBlockBoundStatement forStatement) {
        List<BoundStatement> statements = new ArrayList<>();

        Label startLabel = newLabel();
        Label endLabel = newLabel();

        statements.addAll(boundStatement(forStatement.initializer()));

        statements.add(new LabelBoundStatement(startLabel));

        statements.add(new ConditionalGotoBoundStatement(forStatement.condition().expression(), endLabel));

        List<BoundStatement> whileBodyStatements = blockBoundStatement(forStatement.forBlockBody());
        statements.addAll(whileBodyStatements);

        statements.addAll(boundStatement(forStatement.stepper()));

        statements.add(new GotoBoundStatement(startLabel));

        statements.add(new LabelBoundStatement(endLabel));

        return statements;
    }

    private Label newLabel() {
        return new Label("label_" + labelCounter++);
    }
}
