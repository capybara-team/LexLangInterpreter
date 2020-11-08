package lexlang;

import java.util.Objects;

public class Value {
    final Object value;

    public static Value VOID = new Value(new Object());


    public Value(Object value) {
        this.value = value;
    }

    public Integer getInt() {
        return (Integer) value;
    }

    public Float getFloat() {
        return (Float) value;
    }

    public Character getChar() {
        return (Character) value;
    }

    public Boolean getBool() {
        return (Boolean) value;
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public boolean isInt() {
        return value instanceof Integer;
    }

    @Override
    public boolean equals(Object o) {
        if (value == o) return true;
//        if (value == null || o == null || o.getClass() != value.getClass()) return false; // TODO: check why this exists
        Value value1 = (Value) o;
        return this.value.equals(value1.value);
    }

    @Override
    public int hashCode() {
        if (value == null) return 0;
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
