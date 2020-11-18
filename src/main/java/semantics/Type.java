/*
  Maxwell Souza 201435009
  Rodolpho Rossete 201435032
 */


package semantics;

import lexlang.Value;

import java.util.Objects;

public class Type extends Value {
    public final DefaultTypes type;
    public int depth = 0;
    DataDeclaration data = null;

    public Type(DefaultTypes type) {
        super(null);
        this.type = type;
    }

    public Type(DefaultTypes type, int depth) {
        super(null);
        this.type = type;
        this.depth = depth;
    }

    public Type(DefaultTypes type, int depth, DataDeclaration data) {
        super(null);
        this.type = type;
        this.depth = depth;
        this.data = data;

    }

    public DefaultTypes getType() {
        return type;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public DataDeclaration getDataType() {
        return data;
    }

    public boolean isArray() {
        return depth > 0;
    }

    public boolean isData() {
        return data != null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Type)) return false;
        Type type1 = (Type) o;
        return getDepth() == type1.getDepth() &&
                getType() == type1.getType() &&
                getDataType() == type1.getDataType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getDepth(), getDataType().getId());
    }

    @Override
    public String toString() {
        StringBuilder arrayDepth = new StringBuilder();
        for (int i = 0; i < depth; i++) arrayDepth.append("[]");
        if (isData()) return data.getId() + arrayDepth;
        return type + arrayDepth.toString();
    }
}
