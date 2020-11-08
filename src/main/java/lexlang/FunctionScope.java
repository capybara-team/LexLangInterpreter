package lexlang;

public class FunctionScope {

    private Scope scope = new Scope();
    private FunctionScope parentFunction = null;

    public FunctionScope getParent() {
        return parentFunction;
    }

    public FunctionScope(FunctionScope parentFunction) {
        this.parentFunction = parentFunction;
    }

    public FunctionScope() { }

    public Value getVariable(String name) {
        return scope.getVariable(name);
    }

    public Value setVariable(String name, Value value) {
        return scope.setVariable(name, value);
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
