package lexlang;

public class Main {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Lang compiler V0.0.1 - Outubro de 2020");
			System.out.println("Ação: ");
			System.out
					.println(" <caminho_arquivo> : Interpreta um arquivo específico.");

		}
		if (args.length == 1){
			LangParser.interpreterFile(args[0]);
		}

	}

}
