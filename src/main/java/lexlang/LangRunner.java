/*
  Maxwell Souza 201435009
  Rodolpho Rossete 201435032
 */


package lexlang;


import generator.CodeGenerator;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import semantics.SemanticAnalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class LangRunner {


    public static void interpreterFile(String path) {
        ParseTree tree = generateTree(openFile(path));
        SemanticAnalyzer analyzer = analyzeFile(tree);
        LangInterpreter langInterpreter = new LangInterpreter(analyzer);
        langInterpreter.run();
    }

    public static SemanticAnalyzer analyzeFile(ParseTree tree) {
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.run(tree);
        return analyzer;
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

    public static void genereteJavaCode(String langPath, String javaPath){

        ParseTree tree = generateTree(openFile(langPath));
        SemanticAnalyzer analyzer = analyzeFile(tree);
        STGroup group = new STGroupFile("./template/java.stg");
        CodeGenerator codeGenerator = new CodeGenerator(analyzer,group);
        String javaCode = codeGenerator.run();
        createJavaCodeFile(javaCode, javaPath);

    }

    private static void createJavaCodeFile(String javaCode, String javaPath) {

        try {
            FileWriter javaFile = new FileWriter(javaPath);

            javaFile.append(javaCode);
            javaFile.close();
            System.out.println("File created: " + javaPath);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

   /* private static String getFileWithoutExtension(String langPath) {
        return langPath.substring(0, langPath.length()-4);
    }
*/
}
