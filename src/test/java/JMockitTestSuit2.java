import mockit.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fallen on 1/27/14.
 */
public class JMockitTestSuit2 {
    public static class Dependency {
        public int doFoo(int a) {
            return a+1;
        }

        public void doFooVoid(Integer a) {
            System.out.println("fooVoid called with: " + a);
        }
    }

    public static class Dependant {
        private Dependency dep = new Dependency();
        public int doFoo2(int a) {
            return dep.doFoo(dep.doFoo(a));
        }

        public void doFooVoid2(Integer a) {
            dep.doFooVoid(a);
        }
    }

    // @Injectable
    @Test
    public void test_1(@Injectable final Dependency depcy) {
        new NonStrictExpectations() {{
            depcy.doFoo(anyInt); result = 33;
        }};

        Dependant d = new Dependant();
        System.out.println("test 1(0): " + d.doFoo2(2));
        d.dep = depcy;
        System.out.println("test 1(1): " + d.doFoo2(2));
    }

    /** withXyz; there can be added custom matchers */
    @Test
    public void test_2(@Mocked final Dependency depcy) {
        new NonStrictExpectations() {{
            depcy.doFoo(withEqual(2)); result = 33;
            depcy.doFoo(withEqual(33)); result = 600;
        }};

        System.out.println("test 2: " + new Dependant().doFoo2(2));
    }

    /** null-matcher  */
    @Test
    public void test_3(@Mocked final Dependency depcy) {
        new NonStrictExpectations() {{
            depcy.doFooVoid(null);
        }};

        Dependant d = new Dependant();
        System.out.println("test 3: ");
        d.doFooVoid2(2);
    }

    /** varargs */
    @SuppressWarnings("unused")
    class VarArgs
    {
        public void varsOnly(int... ints) {}
        public void mixed(String arg0, int... ints) {}
    }

    @SuppressWarnings("NullArgumentToVariableArgMethod")
    @Test
    public void test_4_expectInvocationWithNoVarArgs(@Mocked final VarArgs mock)
    {
        new Expectations() {{
            mock.varsOnly();
            mock.varsOnly(null);
            mock.varsOnly((int[]) any);
            mock.mixed("abcd");
            mock.mixed("abcd", null);
        }};

        mock.varsOnly();
        mock.varsOnly(null);
        mock.varsOnly(1,2,3);
        mock.mixed("abcd");
        mock.mixed("abcd", null);
    }
}
