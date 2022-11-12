public class SerialTester {

    private final SerialManager serialManager = new SerialManager();


    public SerialTester() {

    }

    public void connect(String portName) {
        serialManager.connect(serialManager.getPortByName(portName));
    }

    public void test() throws InterruptedException {
        for(int i = 0; i < 10; i++) {
            Thread.sleep(5000);
            serialManager.writeData("TEST");

        }
    }


}
