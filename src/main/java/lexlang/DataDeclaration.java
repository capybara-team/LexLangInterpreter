package lexlang;

import java.util.ArrayList;
import java.util.List;

public class DataDeclaration {
    private String id;

    List<DataType> types = new ArrayList<>();

    public DataDeclaration(LexLangParser.DataContext ctx) {
        id = ctx.ID().getText();
        if (ctx.decl() != null)
            for (LexLangParser.DeclContext declContext : ctx.decl())
                // TODO: Fix array
                types.add(new DataType(declContext.ID().getText(), declContext.type().getText()));

    }

    public List<DataType> getTypes() {
        return types;
    }

    public String getId() {
        return id;
    }

    public static class DataType {
        String name;
        String type;

        public DataType(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}