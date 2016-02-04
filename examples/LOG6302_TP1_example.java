public class LOG6302_TP1_example {

    public static void main(String[] args) {
        Foo foo = new Foo();

        if (foo.bar() == 17) {
            while (true) {
                if (foo.x == 42) {
                    break;
                }
                continue;
            }
        }
    }

    public static class Foo {
        public int x;

        public Integer bar() {
            if (false)
                return 42;
            for (int i = 0; i < -1; i++);
            return -1;
        }
    }

}
