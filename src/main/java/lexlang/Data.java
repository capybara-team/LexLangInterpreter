package lexlang;

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
        return "Data{" +
                "type=" + type +
                ", values=" + values +
                '}';
    }

    public Value get(Object key) {
        return values.get(key);
    }

    public Value put(String key, Value value) {
        return values.put(key, value);
    }
}
