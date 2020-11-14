package lexlang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FunctionManager {
    HashMap<String, List<FunctionDeclaration>> functions = new HashMap<>();

    public FunctionDeclaration addFunction(LexLangParser.FuncContext ctx) {
        FunctionDeclaration f = new FunctionDeclaration(ctx);

        if (!functions.containsKey(f.getId())) {
            functions.put(f.getId(), new ArrayList<>() {{
                add(f);
            }});
            return f;
        }
        // TODO: support function overload
        else throw new LangException("Multiple versions of a function is still not supported. ");

//        return f;
    }

    FunctionDeclaration getFunction(String name) {
        if (!functions.containsKey(name)) throw new LangException("Function '" + name + "' not found");
        return functions.get(name).get(0);
    }
}
