/**
 Maxwell Souza 201435009
 Rodolpho Rossete 201435032
 */


package lexlang;


import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.IOException;

public class LangRunner {

    public static void interpreterFile(String path) {
        LangInterpreter langInterpreter = new LangInterpreter();
        langInterpreter.run(generateTree(openFile(path)));
    }

    public static ANTLRFileStream openFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            System.err.println("File '" + file.getPath() + "' not found");
            System.exit(1);
        }
        try {
            return new ANTLRFileStream(file.getPath());
        } catch (IOException e) {
            System.err.println("Error opening File '" + file.getPath() + "'");
            e.printStackTrace();
        }
        return null;
    }

    public static ParseTree generateTree(ANTLRFileStream file) {
        LexLangLexer lexer = new LexLangLexer(file);
        LexLangParser parser = new LexLangParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.prog();
        // System.out.println(tree.getText());
        if (parser.getNumberOfSyntaxErrors() > 0)
            System.exit(1);
        return tree;
    }

}