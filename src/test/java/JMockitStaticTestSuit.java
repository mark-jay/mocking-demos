import mockit.*;
import org.junit.Test;

/**
 * Created by fallen on 1/29/14.
 */
public class JMockitStaticTestSuit {
    public static int dependency(int a) {
        return a+1;
    }

    public static int dependant(int a) {
        return dependency(dependency(a));
    }

    @Test
    public void test_1() {
        new MockUp<JMockitStaticTestSuit>()
        {
            @Mock
            public int dependency(int a) // this method is static
            {
                return 33;
            }
        };
        System.out.println("test 1:" + dependant(0));
    }
}
