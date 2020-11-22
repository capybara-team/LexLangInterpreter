/*
 Maxwell Souza 201435009
 Rodolpho Rossete 201435032
 */


package lexlang;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Use -h or --help to get system functionality ");

        }
        if (args.length == 2 && (args[0].equals("-i") || args[0].equals("--interpret"))) {
            LangRunner.interpreterFile(args[1]);
        }
        if (args.length == 3 && (args[0].equals("-gc")   || args[0].equals("--generetecode")  )){
            LangRunner.genereteJavaCode(args[1], args[2]);
        }
        if(args.length == 1 && (args[0].equals("-h")  || args[0].equals("-help"))){
            System.out.println("Lang interpreter V0.0.4");
            System.out.println("Actions: ");
            System.out
                    .println("-i or --interpret: To interpret the lang code. Like: " +
                            "\n\t-i <lang_file_location>"+
                            "\n\t--interpret <lang_file_location>");
            System.out
                    .println("\n-gc or --generetecode: To generete the Java code. Like: " +
                            "\n\t-gc <lang_file_location> <java_file_location>"+
                            "\n\t--generetecode <lang_file_location> <java_file_location>");

        }


    }

}
