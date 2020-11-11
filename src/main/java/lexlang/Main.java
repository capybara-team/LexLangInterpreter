/**
 Maxwell Souza 201435009
 Rodolpho Rossete 201435032
 */


package lexlang;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Lang interpreter V0.0.1");
            System.out.println("Ação: ");
            System.out
                    .println(" <caminho_arquivo> : Interpreta um arquivo específico.");

        }
        if (args.length == 1) {
            LangRunner.interpreterFile(args[0]);
        }

    }

}
