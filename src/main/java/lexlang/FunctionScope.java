package lexlang;

import org.antlr.v4.runtime.tree.TerminalNode;

public class FunctionScope {

    private Scope scope = new Scope();
    private FunctionScope parentFunction = null;

    public FunctionScope getParent() {
        return parentFunction;
    }

    public FunctionScope(FunctionScope parentFunction) {
        this.parentFunction = parentFunction;
    }

    public FunctionScope() {
    }

    public Value getVariable(String name) {
        return scope.getVariable(name);
    }

    public Value getVariable(TerminalNode name) {
        return getVariable(name.getText());
    }

    public Value setVariable(String name, Value value) {
        return scope.setVariable(name, value);
    }

    public Value setVariable(TerminalNode name, Value value) {
        return setVariable(name.getText(), value);
    }

    public FunctionScope pushScope() {
        scope = new Scope(scope);
        return this;
    }

    public FunctionScope popScope() {
        scope = scope.getParent();
        return this;
    }
}
