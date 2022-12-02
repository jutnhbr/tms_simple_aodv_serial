package model.messageTypes;

public class RREQ {

    private final byte type = 1;
    private byte flags;
    private byte hopCount;
    private byte req;
    private byte destAddr;
    private byte destSeq;
    private byte sourceAddr;
    private byte sourceSeq;

    public RREQ(byte flags, byte hopCount, byte req, byte destAddr, byte destSeq, byte sourceAddr, byte sourceSeq) {
        this.flags = flags;
        this.hopCount = hopCount;
        this.req = req;
        this.destAddr = destAddr;
        this.destSeq = destSeq;
        this.sourceAddr = sourceAddr;
        this.sourceSeq = sourceSeq;
    }

    public byte getType() {
        return type;
    }

    public byte getFlags() {
        return flags;
    }

    public void setFlag(byte flag) {
        this.flags = flag;
    }

    public byte getHopCount() {
        return hopCount;
    }

    public void setHopCount(byte hopCount) {
        this.hopCount = hopCount;
    }

    public byte getReq() {
        return req;
    }

    public void setReq(byte req) {
        this.req = req;
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

    public byte getSourceSeq() {
        return sourceSeq;
    }

    public void setSourceSeq(byte sourceSeq) {
        this.sourceSeq = sourceSeq;
    }

    @Override
    public String toString() {
        return "RREQ{" +
                "type=" + type +
                ", flags=" + flags +
                ", hopCount=" + hopCount +
                ", req=" + req +
                ", destAddr=" + destAddr +
                ", destSeq=" + destSeq +
                ", sourceAddr=" + sourceAddr +
                ", sourceSeq=" + sourceSeq +
                '}';
    }
}
