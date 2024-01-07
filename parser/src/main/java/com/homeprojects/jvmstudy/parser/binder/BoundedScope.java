package com.homeprojects.jvmstudy.parser.binder;

import com.homeprojects.jvmstudy.parser.binder.statements.MethodDeclarationBoundStatement;
import com.homeprojects.jvmstudy.parser.types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BoundedScope {

    private final List<Map<String, Type>> variables;

    private final List<Type> methodTypes;

    private final List<Map<String, MethodDeclarationBoundStatement>> methods;

    public BoundedScope() {
        this.variables = new ArrayList<>();
        this.methodTypes = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    public void newScope() {
        variables.add(new HashMap<>());
        methods.add(new HashMap<>());
    }

    public void addVariable(String name, Type type) {
        variables.getLast().put(name, type);
    }

    public void addMethodType(Type type) {
        methodTypes.add(type);
    }

    public void addMethod(String name, MethodDeclarationBoundStatement statement) {
        methods.getLast().put(name, statement);
    }

    public void remove() {
        variables.removeLast();
        methods.removeLast();
    }

    public boolean isVariableInLastScope(String name) {
        return variables.getLast().containsKey(name);
    }

    public Optional<Type> typeOfVariable(String name) {
        for (int i = variables.size() - 1; i >= 0; i--) {
            if (variables.get(i).containsKey(name)) {
                return Optional.of(variables.get(i).get(name));
            }
        }
        return Optional.empty();
    }

    public Optional<MethodDeclarationBoundStatement> findMethod(String name) {
        for (int i = methods.size() - 1; i >= 0; i--) {
            if (methods.get(i).containsKey(name)) {
                return Optional.of(methods.get(i).get(name));
            }
        }
        return Optional.empty();
    }

    public void removeMethodType() {
        methodTypes.removeLast();
    }

    public Type currentMethodType() {
        return methodTypes.getLast();
    }
}
