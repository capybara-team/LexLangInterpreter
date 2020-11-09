package lexlang;

import java.util.HashMap;

public class Scope {
    private Scope parent = null;
    private HashMap<String, Value> memory = new HashMap<>();

    public Scope() {
    }

    public Scope(Scope parent) {
        this.parent = parent;
    }

    public Value getVariable(String name) {
        Value val = memory.get(name);
        if (val != null) return val;
        if (parent != null) return parent.getVariable(name);
        else throw new RuntimeException("Cannot find variable: " + name);
    }

    private Value updateVariable(String name, Value value) {
        Value oldVal = memory.get(name);
        if (oldVal != null) return memory.put(name, value);
        else if (parent != null)
            return parent.updateVariable(name, value);
        else return null;
    }

    public Value setVariable(String name, Value value) {
        Value result = updateVariable(name, value);
        if (result == null)
            return memory.put(name, value);
        return result;
    }

    public Scope getParent() {
        return parent;
    }
}
