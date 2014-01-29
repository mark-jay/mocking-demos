import mockit.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fallen on 1/27/14.
 */
public class JMockitTestSuit1 {
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
                return -1;
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

    /**
     * by default verifications are not in order. To achieve that we can use VerificationsInOrder
     * or like that:
     * new VerificationsInOrder() {{
         abc.methodThatNeedsToExecuteFirst();
         unverifiedInvocations(); // Invocations not verified must come here...
         xyz.method1();
         abc.method2();
         unverifiedInvocations(); // ... and/or here.
         xyz.methodThatNeedsToExecuteLast();
       }};
     so we can achieve some relational order. Especially using more than 1 Verification block

     one more example(can also be used FullVerificationsInOrder instead):
     new FullVerifications() {{
         // Verifications here are unordered, so the following invocations could be in any order.
         mock.setSomething(anyInt); // verifies two actual invocations
         mock.setSomethingElse(anyString);
         mock.save(); // if this verification (or any other above) is removed the test will fail
     }};

     new FullVerifications(mock1) {{ // only mock1 is verified
         mock1.prepare();
         mock1.setSomething(anyInt);
         mock1.editABunchMoreStuff();
         mock1.save(); times = 1;
     }};

     // Will verify that no invocations other than to "doSomething()" occurred on mock2:
     new FullVerifications(mock2) {};

     new FullVerifications() {{
         mock.getData(); minTimes = 0; // calls to getData are allowed
     }};
     * @param depcy1
     */
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

    /** the same but with interfaces */
    @Test
    public void test_5_1() {
        Dependant1 dependant1 = new Dependant1();

        dependant1.dep = new MockUp<Dependency1>() {
            @Mock(invocations = 2) // (the invocation count constraint is optional)
            public int doFoo(int a) {
                Assert.assertTrue(a > 0);
                return a+1;
            }
            // Other mock or regular methods...
        }.getMockInstance();

        System.out.println("test 5: " + dependant1.doFoo2(2));
    }

    // test on exact instance
    @Test
    public void test_6(@Mocked final Dependency1 depcy1_1, @Mocked final Dependency1 depcy1_2) {
        new NonStrictExpectations() {{
            onInstance(depcy1_1).doFoo(anyInt); result = 33;
            onInstance(depcy1_2).doFoo(anyInt); result = 55;
            // or since there are two same depcies we can do it implicitly:
//            depcy1_1.doFoo(anyInt); result = 33;
//            depcy1_2.doFoo(anyInt); result = 55;
        }};

        Dependant1 d = new Dependant1();
        System.out.println("test 6(0): " + d.doFoo2(2));
        d.dep = depcy1_1;
        System.out.println("test 6(1): " + d.doFoo2(2));
        d.dep = depcy1_2;
        System.out.println("test 6(2): " + d.doFoo2(2));
    }
}
