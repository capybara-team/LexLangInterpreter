/**
 Maxwell Souza 201435009
 Rodolpho Rossete 201435032
 */


package lexlang;

import semantics.DataDeclaration;

import java.util.HashMap;

public class Data {
    DataDeclaration type;
    HashMap<String, Value> values = new HashMap<>();

    public Data(DataDeclaration type) {
        this.type = type;
        for (DataDeclaration.DataType dataType : type.getTypes())
            values.put(dataType.name, new Value(null));

    }

    @Override
    public String toString() {
        return type.getId() + ' ' + values;
    }

    public Value get(String key) {
        return values.get(key);
    }

    public Value put(String key, Value value) {
        return values.put(key, value);
    }
}
