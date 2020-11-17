package semantics;

import lexlang.LexLangParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FunctionManager {
    HashMap<String, List<FunctionDeclaration>> functions = new HashMap<>();

    public HashMap<String, List<FunctionDeclaration>> getFunctions() {
        return functions;
    }

    public FunctionDeclaration addFunction(LexLangParser.FuncContext ctx) {
        FunctionDeclaration f = new FunctionDeclaration(ctx);

        if (!functions.containsKey(f.getId())) {
            functions.put(f.getId(), new ArrayList<>());
        }
        functions.get(f.getId()).add(f);

        return f;
    }

    public FunctionDeclaration getFunction(String name) {
        if (!functions.containsKey(name)) throw new LangException("Function '" + name + "' not found");
        for (FunctionDeclaration functionDeclaration : functions.get(name))
            if (functionDeclaration.getArguments().size() == 0)
                return functionDeclaration;

        return functions.get(name).get(0);
    }

    public List<FunctionDeclaration> getFunctions(String name) {
        if (!functions.containsKey(name)) throw new LangException("Function '" + name + "' not found");
        return functions.get(name);
    }
}
