package model.messageTypes;

import org.uncommons.maths.binary.BitString;

public class RREP {

    private final BitString type = new BitString("000010");
    private BitString lifetime;
    private BitString destAddr;
    private BitString destSeq;
    private BitString sourceAddr;

    private BitString hopCount;

    public RREP(BitString lifetime, BitString destAddr, BitString destSeq, BitString sourceAddr, BitString hopCount) {
        this.lifetime = lifetime;
        this.destAddr = destAddr;
        this.destSeq = destSeq;
        this.sourceAddr = sourceAddr;
        this.hopCount = hopCount;
    }


    public BitString getType() {
        return type;
    }

    public BitString getLifetime() {
        return lifetime;
    }

    public void setLifetime(BitString lifetime) {
        this.lifetime = lifetime;
    }

    public BitString getDestAddr() {
        return destAddr;
    }

    public void setDestAddr(BitString destAddr) {
        this.destAddr = destAddr;
    }

    public BitString getDestSeq() {
        return destSeq;
    }

    public void setDestSeq(BitString destSeq) {
        this.destSeq = destSeq;
    }

    public BitString getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(BitString sourceAddr) {
        this.sourceAddr = sourceAddr;
    }

    public BitString getHopCount() {
        return hopCount;
    }

    public void setHopCount(BitString hopCount) {
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
