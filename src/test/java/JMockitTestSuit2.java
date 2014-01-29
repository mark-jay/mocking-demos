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
    }

    public static class Dependant {
        private Dependency dep = new Dependency();
        public int doFoo2(int a) {
            return dep.doFoo(dep.doFoo(a));
        }
    }

    @Test
    public void test_1(@Injectable final Dependency depcy) {
        new NonStrictExpectations() {{
            depcy.doFoo(anyInt); result = 33;
        }};

        Dependant d = new Dependant();
        System.out.println("test 7(0): " + d.doFoo2(2));
        d.dep = depcy;
        System.out.println("test 7(1): " + d.doFoo2(2));
    }
}
