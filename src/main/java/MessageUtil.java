public class MessageUtil {

    private MessageModes currentMode;


    public MessageUtil() {
        currentMode = MessageModes.CARRIAGE_RETURN_NEW_LINE;
    }

    public void changeMessageMode(MessageModes mode) {
        currentMode = mode;
    }
    public String message(String message) {
        return message + currentMode.getModeSymbol();
    }
    public MessageModes getCurrentMode() {
        return currentMode;
    }



    @Override
    public String toString() {
        return "MessageUtil{" +
                "currentMode=" + currentMode +
                '}';
    }
}
