/*
  Maxwell Souza 201435009
  Rodolpho Rossete 201435032
 */

package semantics;

public class Validator {
    static final Type INT = new Type(DefaultTypes.Int);
    static final Type FLOAT = new Type(DefaultTypes.Float);
    static final Type BOOL = new Type(DefaultTypes.Bool);

    static void isNumber(Type type) {
        if (!type.equals(INT) && !type.equals(FLOAT))
            throw new LangException("Value must be a 'Int' or 'Float'. received '" + type + "'");
    }

    static void isInt(Type type) {
        if (!type.equals(INT))
            throw new LangException("Value must be a 'Int'. received '" + type + "'");
    }

    static void isBool(Type type) {
        if (!type.equals(BOOL))
            throw new LangException("Value must be a 'Bool'. received '" + type + "'");
    }

    static void compareTypes(Type t1, Type t2) {
        if (!t1.equals(t2))
            throw new LangException("Both values should be the same type. Operation mixed '" + t1 + "' and '" + t2 + "'");
    }

    static void canSet(Type prop, Type value) {
        if (!prop.equals(value))
            throw new LangException("Cannot set value: Expected '" + prop + "', received '" + value + "'");
    }

    static void canAccess(Type data, String prop) {
        String prefix = "Cannot access property '" + prop + "'";
        if (!data.isData())
            throw new LangException(prefix + " of non-data type '" + data + "'");
        if (data.isArray())
            throw new LangException(prefix + " of array of data '" + data + "'");
        if (data.getDataType().getType(prop) == null)
            throw new LangException(", Data " + data + " doesn't have a property '" + prop + "'");
    }
}
