package data;

public enum AT {

    AT("AT"),
    AT_CFG("AT+CFG="),
    AT_RST("AT+RST"),
    AT_VER("AT+VER"),
    AT_SEND("AT+SEND="),
    AT_RSSI("AT+RSSI?"),
    AT_RX("AT+RX"),
    AT_ADDR("AT+ADDR?");

    private final String command;

    AT(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

}

