package lexlang;

import java.util.ArrayList;
import java.util.List;

public class DataDeclaration {
    private final String id;

    List<DataType> types = new ArrayList<>();

    public DataDeclaration(LexLangParser.DataContext ctx) {
        id = ctx.ID().getText();
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

    public static class DataType {
        String name;
        LexLangParser.TypeContext type;

        public DataType(String name, LexLangParser.TypeContext type) {
            this.name = name;
            this.type = type;
        }
    }
}