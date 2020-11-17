/**
 Maxwell Souza 201435009
 Rodolpho Rossete 201435032
 */


package lexlang;

import java.util.HashMap;

public class Scope {
    private Scope parent = null;
    private final HashMap<String, Value> memory = new HashMap<>();

    public Scope() {
    }

    public Scope(Scope parent) {
        this.parent = parent;
    }

    public Value getVariable(String name) {
        Value val = memory.get(name);
        if (val != null) return val;
        if (parent != null) return parent.getVariable(name);
        else return null;
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
