package lexlang;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO: support overload
public class FunctionDeclaration {
    private String id;

    List<FunctionArgument> arguments = new ArrayList<>();

    List<String> returnTypes = new ArrayList<>();

    LexLangParser.FuncCmdsContext commands;

    public FunctionDeclaration(LexLangParser.FuncContext ctx) {
        id = ctx.ID().getText();
        if (ctx.params() != null) {
            for (int i = 0; i < ctx.params().ID().size(); i++) {
                arguments.add(
                        // TODO: Fix array
                        new FunctionArgument(
                                ctx.params().ID().get(i).getText(),
                                ctx.params().type().get(i).getText())
                );
            }
        }
        for (LexLangParser.TypeContext typeContext : ctx.type()) {
            returnTypes.add(typeContext.getText());
        }
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
        String type;

        public FunctionArgument(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}