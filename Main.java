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
    static Object[] _ret;

    public static int read() {
        return Integer.valueOf(_reader.nextLine());
    }

    public static void main(String args[]) {
        main();
    }

    static Object[] genPerson(int ammount) {
        Person[] p;
        int i;
        p = new Person[ammount];
        i = 0;
        int _cond_0 = ammount;
        for(int _i_0 = 0; _i_0 < _cond_0; _i_0++) {
            p[i] = new Person();
            i = i + 1;
        }
        return new Object[]{p};
    }

    static Object[] foo() {
        return new Object[]{1, 4.2f, 'a'};
    }

    static void main() {
        Person[] a;
        int x;
        float y;
        char z;
        int k;
        a = (Person[]) genPerson(10)[0];
        a[0].age = 20;
        System.out.print(a);
        _ret = foo();
        x = (int)_ret[0];
        y = (float)_ret[1];
        z = (char)_ret[2];
        _ret = foo();
        k = (int)_ret[0];
        y = (float)_ret[1];
    }
}