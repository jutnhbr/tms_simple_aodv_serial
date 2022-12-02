package model.messageTypes;

public class RREP {

    private final byte type = 2;
    private byte lifetime;
    private byte destAddr;
    private byte destSeq;
    private byte sourceAddr;
    private byte hopCount;

    public RREP(byte lifetime, byte destAddr, byte destSeq, byte hopCount, byte sourceAddr) {
        this.lifetime = lifetime;
        this.destAddr = destAddr;
        this.destSeq = destSeq;
        this.hopCount = hopCount;
        this.sourceAddr = sourceAddr;
    }


    public byte getType() {
        return type;
    }

    public byte getLifetime() {
        return lifetime;
    }

    public void setLifetime(byte lifetime) {
        this.lifetime = lifetime;
    }

    public byte getDestAddr() {
        return destAddr;
    }

    public void setDestAddr(byte destAddr) {
        this.destAddr = destAddr;
    }

    public byte getDestSeq() {
        return destSeq;
    }

    public void setDestSeq(byte destSeq) {
        this.destSeq = destSeq;
    }

    public byte getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(byte sourceAddr) {
        this.sourceAddr = sourceAddr;
    }

    public byte getHopCount() {
        return hopCount;
    }

    public void setHopCount(byte hopCount) {
        this.hopCount = hopCount;
    }

    @Override
    public String toString() {
        return "RREP{" +
                "type=" + type +
                ", lifetime=" + lifetime +
                ", destAddr=" + destAddr +
                ", destSeq=" + destSeq +
                ", sourceAddr=" + sourceAddr +
                ", hopCount=" + hopCount +
                '}';
    }
}
