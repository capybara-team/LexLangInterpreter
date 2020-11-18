# Interpretador LexLang

Um Analizador e Interpretador para a linguagem LANG, desenvolvido na disciplina **DCC045-2020.1-A - TEORIA DOS COMPILADORES - DEPTO DE CIENCIA DA COMPUTACAO /ICE - UFJF**

## Sobre

O projeto foi desenvolvido em Java, usando o gerenciador de dependências Maven. Foi usada a ferramenta ANTLR4, para geração do analisador Léxico e Sintático, bem como o Visitor extendido pelo analizador e interpretador.

Foi gerado anteriormente um arquivo [LexLang.g4](src/main/antlr4/lexlang/LexLang.g4), o qual contém a definição da linguagem, léxica e sintática.

## Compilar

Basta executar o arquivo build.sh, ou executar os comandos:

```shell script
mvn clean install
```

## Uso

O programa recebem por parâmetro o caminho do arquivo de programa lang a ser executado.

```bash
java -jar target/compile-V-0.0.1-jar-with-dependencies.jar caminho/do/programa.lang
```
