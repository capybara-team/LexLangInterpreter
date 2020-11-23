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

caso queira converter em código java, basta informar a tag `-gc`

```bash js
java -jar target/compile-V-0.0.1-jar-with-dependencies.jar -gc caminho/do/programa.lang
```

## Exemplo de programa

```c++
-- Comentário em linha

{-

Comentário em bloco

-}

-- Declaração de estrutura
data Person {
    gender :: Char;
    age :: Int;
    relatives :: Person[];
}

-- função principal
main(){

    read times;                 -- Le inteiro do teclado
    iterate(times)              -- for primitivo
        print hello()[0];                -- chamada de função

    me = new Person;            -- Instancia estrutura
    me.age = 24;                -- atribuição de propriedade
    me.gender = 'm';
    me.relatives = new Person[10];
    me.relatives[0] = new Person;
    me.relatives[0].age = 40;
    me.relatives[0].gender = 'f';

    print fibonacci(9)[0];  -- get return of a function
    ln();

    divide(10, 3)<result, resto>;
    print result;
    ln();
    print resto;
    ln();

}

fibonacci(n :: Int) : Int
{
    if (n < 1)
        return n;
    if (n == 1)
        return n;
    return fibonacci(n-1)[0] + fibonacci(n-2)[0];
}

divide(a :: Int, b :: Int) : Int, Int {
    return a / b, a % b;
}
-- sobrecarga
divide(a :: Float, b :: Float) : Float, Float {
    return a / b, a % b;
}

ln(){ print '\n'; }

hello() : Char[] {
    message = new Char[13];     -- Instancia array
    message[0] = 'h';
    message[1] = 'e';
    message[2] = 'l';
    message[3] = 'l';
    message[4] = 'o';
    message[5] = ' ';
    message[6] = 'w';
    message[7] = 'o';
    message[8] = 'r';
    message[9] = 'l';
    message[10] = 'd';
    message[11] = '!';
    message[12] = '\n';
    return message;
}
```