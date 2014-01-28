import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
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
    public void myFirstTestMethod() {
        new NonStrictExpectations() {{
            depcy.doFoo(anyInt); result = 10;
        }};

        System.out.println(new Dependant().doFoo2(2));
        new Verifications() {{

        }};
    }
}
