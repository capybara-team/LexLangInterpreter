/**
 * Maxwell Souza 201435009
 * Rodolpho Rossete 201435032
 */


package lexlang;

import java.util.ArrayList;
import java.util.List;

// TODO: Store Type instance instead of tree node
public class DataDeclaration {
    private final String id;

    List<DataType> types = new ArrayList<>();

    public DataDeclaration(LexLangParser.DataContext ctx) {
        id = ctx.ID().getText();
        if (!Character.isUpperCase(id.charAt(0)))
            throw new LangException("Data declaration name should start with uppercase. Received '" + id + "'", ctx);
        if (ctx.decl() != null)
            for (LexLangParser.DeclContext declCtx : ctx.decl())
                types.add(new DataType(declCtx.ID().getText(), declCtx.type()));

    }

    public List<DataType> getTypes() {
        return types;
    }

    public String getId() {
        return id;
    }

    public DataType getType(String name) {
        for (DataType type : types)
            if (type.name.equals(name))
                return type;
        return null;
    }

    public static class DataType {
        public String name;
        public LexLangParser.TypeContext type;

        public DataType(String name, LexLangParser.TypeContext type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String toString() {
            return "DataType{" +
                    "name='" + name + '\'' +
                    ", type=" + type.getText() +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DataType{" + id + '}';
    }
}