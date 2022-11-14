public class ApplicationTester {

    public static void main(String[] args) throws InterruptedException {
        TestEnvironment testEnvironment = new TestEnvironment();
        SerialManager serialTester = new SerialManager();
        testEnvironment.start(serialTester);
    }


}
