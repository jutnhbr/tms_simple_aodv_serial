package model.messageTypes;

import org.uncommons.maths.binary.BitString;

public class DATA {

    private final BitString type = new BitString("000000");
    private BitString message;


    public DATA(BitString message) {
        this.message = message;
    }

    public BitString getType() {
        return type;
    }

    public BitString getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "DATA{" +
                "type=" + type +
                ", message=" + message +
                '}';
    }
}
