/*
  Maxwell Souza 201435009
  Rodolpho Rossete 201435032
 */


package semantics;

import lexlang.LexLangParser;

import java.util.ArrayList;
import java.util.List;

// TODO: store return types and arguments as Type instances
public class FunctionDeclaration {
    private final String id;

    final List<FunctionArgument> arguments = new ArrayList<>();

    final List<LexLangParser.TypeContext> returnTypes = new ArrayList<>();

    final LexLangParser.FuncCmdsContext commands;

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

    public List<LexLangParser.TypeContext> getReturnTypes() {
        return returnTypes;
    }

    public String getId() {
        return id;
    }

    public static class FunctionArgument {
        public final String name;
        public final LexLangParser.TypeContext type;

        public FunctionArgument(String name, LexLangParser.TypeContext type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String toString() {
            return name + " :: " + type.getText();
        }
    }

    @Override
    public String toString() {
        return id + arguments;
    }
}