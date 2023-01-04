package model.messageTypes;

import org.uncommons.maths.binary.BitString;

public class DATA {

    private final BitString type = new BitString("000000");
    private BitString message;
    private BitString destAddr;


    public DATA(BitString message, BitString destAddr) {
        this.message = message;
        this.destAddr = destAddr;
    }

    public BitString getType() {
        return type;
    }

    public BitString getMessage() {
        return message;
    }

    public BitString getDestAddr() {
        return destAddr;
    }

    @Override
    public String toString() {
        return "DATA{" +
                "type=" + type +
                ", message=" + message +
                ", destAddr=" + destAddr +
                '}';
    }
}
