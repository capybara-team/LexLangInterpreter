/**
 Maxwell Souza 201435009
 Rodolpho Rossete 201435032
 */



package lexlang;

public class Value {
    final Object primitive;

    public static Value VOID = new Value(null);
    public static Value EMPTY_CHAR = new Value((Character)Character.MIN_VALUE);

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

    public Object getRawValue() {
        return primitive;
    }

    public Data getData() {
        return (Data) primitive;
    }

    public ArrayValue getArray(){
        return (ArrayValue) primitive;
    }

    @Override
    public boolean equals(Object o) {
        if (primitive == o) return true;
        // TODO: check why this exists
//      if (value == null || o == null || o.getClass() != value.getClass()) return false;
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
        if (this == EMPTY_CHAR)
            return "";
        return String.valueOf(primitive);
    }
}
