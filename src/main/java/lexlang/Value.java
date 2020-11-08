package lexlang;

public class Value {
    final Object primitive;

    public static Value VOID = new Value(new Object());


    public Value(Object value) {
        this.primitive = value;
    }

    public Integer getInt() {
        return (Integer) primitive;
    }

    public Float getFloat() {
        if (primitive instanceof Float) {
            return (Float) primitive;
        }
        return ((Number) primitive).floatValue();
    }

    public Character getChar() {
        return (Character) primitive;
    }

    public Boolean getBool() {
        return (Boolean) primitive;
    }

    public boolean isNumber() {
        return primitive instanceof Number;
    }

    public boolean isInt() {
        return primitive instanceof Integer;
    }

    public Object getRawValue() {
        return primitive;
    }

    @Override
    public boolean equals(Object o) {
        if (primitive == o) return true;
//        if (value == null || o == null || o.getClass() != value.getClass()) return false; // TODO: check why this exists
        Value comparedValue = (Value) o;
        if (comparedValue.getRawValue() instanceof Number && this.primitive instanceof Number)
            return this.getFloat().equals(comparedValue.getFloat());
        return this.primitive.equals(comparedValue.getRawValue());
    }

    @Override
    public int hashCode() {
        if (primitive == null) return 0;
        return this.primitive.hashCode();
    }

    @Override
    public String toString() {
        return String.valueOf(primitive);
    }
}
