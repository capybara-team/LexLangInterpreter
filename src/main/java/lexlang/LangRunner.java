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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
        STGroup group = null;
        try {
            group = new STGroupFile(runner.getSTGPath(), "utf-8", '<', '>' );
        } catch (IOException e) {
            e.printStackTrace();
        }
        CodeGenerator codeGenerator = new CodeGenerator(analyzer, group);
        String javaCode = codeGenerator.run();
        System.out.println(javaCode);
        createJavaCodeFile(javaCode, getFileWithoutExtension(langPath) + ".java");

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

    private String getSTGPath() throws IOException {
        final String path = "java.stg";
        final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        System.out.println(jarFile);
        if(jarFile.isFile()) {
            final JarFile jar = new JarFile(jarFile);
            final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            while(entries.hasMoreElements()) {

                final String name = entries.nextElement().getName();
                if (name.startsWith(path)) {
                    return name;
                }
            }
            jar.close();
        } else {
            final URL url = LangRunner.class.getResource("/" + path);
            if (url != null) {
                try {
                    final File apps = new File(url.toURI());
                    return apps.getPath();
                } catch (URISyntaxException ex) {
                }
            }
        }
        return null;
    }
}
