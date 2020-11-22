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

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class LangRunner {

    public static void interpreterFile(String path) {
        ParseTree tree = generateTree(openFile(path));
        SemanticAnalyzer analyzer = analyzeFile(tree);
        LangInterpreter langInterpreter = new LangInterpreter(analyzer);
        langInterpreter.run();
    }

    public static void genereteJavaCode(String langPath) {
        ParseTree tree = generateTree(openFile(langPath));
        SemanticAnalyzer analyzer = analyzeFile(tree);
        LangRunner runner = new LangRunner();
        STGroup group = new STGroupFile(runner.getSTGPath(), "utf-8", '<', '>' );
        CodeGenerator codeGenerator = new CodeGenerator(analyzer, group);
        String javaCode = codeGenerator.run();
        System.out.println(javaCode);
//        createJavaCodeFile(javaCode, getFileWithoutExtension(langPath) + ".java");

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
        if (parser.getNumberOfSyntaxErrors() > 0)
            System.exit(1);
        return tree;
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

    private static String getFileWithoutExtension(String langPath) {
        return langPath.split("\\.")[0];
    }

    private String getSTGPath(){
        File resourceFile = null;
        URL location = LangRunner.class.getProtectionDomain().getCodeSource().getLocation();
        String codeLocation = location.toString();
        try{
            if (codeLocation.endsWith(".jar")){
                //Call from jar
                Path path = Paths.get(location.toURI()).resolve("../classes/" + "java.stg").normalize();
                resourceFile = path.toFile();
            }else{
                //Call from IDE
                resourceFile = new File(LangRunner.class.getClassLoader().getResource("java.stg").getPath());
            }
        }catch(URISyntaxException ex){
            ex.printStackTrace();
        }
        return resourceFile.getPath();
    }
}
