import control.SerialCLI;
import data.RunModes;

public class Application {

    public static void main(String[] args) throws InterruptedException {

        SerialCLI serialCLI = new SerialCLI();
        serialCLI.execute(RunModes.STANDARD);



    }
}
