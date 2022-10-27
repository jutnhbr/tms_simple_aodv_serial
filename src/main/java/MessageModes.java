public enum MessageModes {
    CARRIAGE_RETURN ("Carriage Return", "\\r"),
    NEW_LINE ("New Line","\\n"),
    CARRIAGE_RETURN_NEW_LINE ("Carriage Return and New Line","\\r\\n");

    private final String modeName;
    private final String modeSymbol;

    private MessageModes(String modeName, String modeSymbol) {
        this.modeName = modeName;
        this.modeSymbol = modeSymbol;

    }

    public String getModeName() {
        return modeName;
    }

    public String getModeSymbol() {
        return modeSymbol;
    }

    @Override
    public String toString() {
        return "MessageModes{" +
                "modeName='" + modeName + '\'' +
                ", modeSymbol='" + modeSymbol + '\'' +
                '}';
    }
}
