import mockit.*;
import org.junit.Test;

/**
 * Created by fallen on 1/27/14.
 */
public class JMockitTestSuite {
    public static class Dependency {
        public int doFoo(int a) {
            return a+1;
        }
    }

    public static class Dependant {
        private Dependency dep = new Dependency();
        public int doFoo2(int a) {
            return dep.doFoo(dep.doFoo(a));
        }
    }

    @Mocked
    Dependency depcy;

    @Test
    public void test_1() {
        new Expectations() {{
            // commenting any of these lines will cause to error. Order matters too
            // but if we used NonStrictExpectations() instead we could use any of them
            new Dependency();
            depcy.doFoo(anyInt); result = 10;
            depcy.doFoo(anyInt); result = 10;
        }};
        System.out.println(new Dependant().doFoo2(2));
    }

    /**
     * or it could even be interface.
     * yet another example.
     */
    public static interface Dependency1 {
        public int doFoo(int a);
    }

    public static class Dependant1 {
        private Dependency1 dep = new Dependency1() {
            @Override
            public int doFoo(int a) {
                return 0;
            }
        };
        public int doFoo2(int a) {
            return dep.doFoo(dep.doFoo(a));
        }
    }

    @Test
    public void test_2(@Capturing final Dependency1 depcy1) {
        new Expectations() {{
            depcy1.doFoo(anyInt); result = 10;
            depcy1.doFoo(anyInt); result = 10;
        }};
        System.out.println("test2: " + new Dependant1().doFoo2(2));
    }
}
