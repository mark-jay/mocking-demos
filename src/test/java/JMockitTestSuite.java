import mockit.*;
import org.junit.Assert;
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
        System.out.println("test 1:" + new Dependant().doFoo2(2));
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
            depcy1.doFoo(anyInt); result = 10; times = 2;
        }};
        System.out.println("test 2: " + new Dependant1().doFoo2(2));
    }

    @Test
    public void test_3(@Capturing final Dependency1 depcy1) {
        System.out.println("test 3: " + new Dependant1().doFoo2(2));
        new Verifications() {{
            depcy1.doFoo(anyInt); times = 2;
            depcy1.doFoo(31337);  times = 0;
        }};
    }

    @Test
    public void test_4(@Capturing final Dependency1 depcy1) {
        System.out.println("test 4: " + new Dependant1().doFoo2(2));
        new Verifications() {{
            depcy1.doFoo(anyInt); minTimes = 0; maxTimes = 10;
        }};
    }

    /**State-oriented mocking*/
    @Test
    public void test_5() {
        new MockUp<Dependency>() {
            @Mock(invocations = 2) // (the invocation count constraint is optional)
            public int doFoo(int a) {
                Assert.assertTrue(a > 0);
                return a+1;
            }
            // Other mock or regular methods...
        };
        System.out.println("test 5: " + new Dependant().doFoo2(2));
    }
}
