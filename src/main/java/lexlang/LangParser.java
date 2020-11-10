package lexlang;


import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;

public class LangParser {

    public static void interpreterFile(String path) {
        File file = new File(path);
        try {
            if (file.exists()) {
                // System.out.println("Executando: " + file.getPath());


                LexLangLexer lexer = new LexLangLexer(new ANTLRFileStream(file.getPath()));
                LexLangParser parser = new LexLangParser(new CommonTokenStream(lexer));
                ParseTree tree = parser.prog();
                LexLangInterpreter lexLangInterpreter = new LexLangInterpreter();
                // System.out.println(tree.getText());
                if (parser.getNumberOfSyntaxErrors() > 0)
                    System.exit(1);
                lexLangInterpreter.run(tree);

            } else {
                System.out.println("O caminho " + file.getPath() + " não existe.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
