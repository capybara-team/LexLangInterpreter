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

    public Value setVariable(String name, Value value) {
        Value oldVal = memory.get(name);
        if (oldVal == null && parent != null) return parent.setVariable(name, value);
        else return memory.put(name, value);
    }

    public Scope getParent() {
        return parent;
    }
}
