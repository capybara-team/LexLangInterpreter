package lexlang;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayValue {
    String type;
    List<Value> values;

    public ArrayValue(int size, String type) {
        this.type = type;
        initValues(size);
    }

    private void initValues(int size) {
        values = new ArrayList<>(size);
        // TODO: initialize with array;
        for (int i = 0; i < size; i++)
            if (type.equals("Char"))
                values.add(Value.EMPTY_CHAR);
            else
                values.add(Value.VOID);
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
        if (type.equals("Char"))
            return values.stream().map(Object::toString).collect(Collectors.joining(""));
        return "ArrayValue{" +
                "type='" + type + '\'' +
                ", values=" + values +
                '}';
    }
}
