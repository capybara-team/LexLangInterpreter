import java.io.InputStreamReader;
import java.util.Scanner;
public class Main {

    static class Person {
        public char gender;
        public int age;
        public float height;
        public Boolean isGay;
        public Person[] relatives;
    }

    static Scanner _reader = new Scanner(new InputStreamReader(System.in));

    public static int read() {
        return Integer.valueOf(_reader.nextLine());
    }

    public static void main(String args[]) {
        main();
    }

    static Object[] bar() {
        return new Object[]{0, 0.0f, 'a'};
    }

    static Object[] foo(int a, float b, char c, boolean d, Person[] e) {
        return new Object[]{0};
    }

    static void main() {
        Person b;
        b = new Person();
        System.out.print(b);
    }
}