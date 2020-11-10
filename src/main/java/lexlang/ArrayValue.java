package lexlang;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayValue {
    String type = null;
    List<Value> values;

    public ArrayValue(int size) {
        this.values = new ArrayList<>(size);
    }

    public Value get(int i) {
        return values.get(i);
    }

    public Value set(int i, Value value) {
        return values.set(i, value);
    }

    public boolean add(Value value) {
        return values.add(value);
    }

    @Override
    public String toString() {
        if(type == "Char")
            return values.stream().map(Object::toString).collect(Collectors.joining(""));
        return "ArrayValue{" +
                "type='" + type + '\'' +
                ", values=" + values +
                '}';
    }
}
