package data;

public enum MessageModes {
    CARRIAGE_RETURN ("Carriage Return", "\r", "\\r"),
    NEW_LINE ("New Line","\n", "\\n"),
    CARRIAGE_RETURN_NEW_LINE ("Carriage Return and New Line","\r\n", "\\r\\n");

    private final String modeName;
    private final String modeSymbol;
    private final String modeSymbolAsString;

    MessageModes(String modeName, String modeSymbol, String modeSymbolAsString) {
        this.modeName = modeName;
        this.modeSymbol = modeSymbol;
        this.modeSymbolAsString = modeSymbolAsString;

    }

    public String getModeName() {
        return modeName;
    }

    public String getModeSymbol() {
        return modeSymbol;
    }

    public String getModeSymbolAsString() {
        return modeSymbolAsString;
    }

    @Override
    public String toString() {
        return "data.MessageModes{" +
                "modeName='" + modeName + '\'' +
                ", modeSymbol='" + modeSymbol + '\'' +
                '}';
    }
}
