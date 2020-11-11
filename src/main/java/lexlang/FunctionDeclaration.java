/**
 Maxwell Souza 201435009
 Rodolpho Rossete 201435032
 */


package lexlang;

import java.util.ArrayList;
import java.util.List;

// TODO: support overload
public class FunctionDeclaration {
    private final String id;

    List<FunctionArgument> arguments = new ArrayList<>();

    List<LexLangParser.TypeContext> returnTypes = new ArrayList<>();

    LexLangParser.FuncCmdsContext commands;

    public FunctionDeclaration(LexLangParser.FuncContext ctx) {
        id = ctx.ID().getText();
        if (ctx.params() != null) {
            for (int i = 0; i < ctx.params().ID().size(); i++) {
                arguments.add(
                        new FunctionArgument(
                                ctx.params().ID().get(i).getText(),
                                ctx.params().type().get(i))
                );
            }
        }
        returnTypes.addAll(ctx.type());
        this.commands = ctx.funcCmds();

    }

    public List<FunctionArgument> getArguments() {
        return arguments;
    }

    public LexLangParser.FuncCmdsContext getCommands() {
        return commands;
    }

    public String getId() {
        return id;
    }

    public static class FunctionArgument {
        String name;
        LexLangParser.TypeContext type;

        public FunctionArgument(String name, LexLangParser.TypeContext type) {
            this.name = name;
            this.type = type;
        }
    }
}