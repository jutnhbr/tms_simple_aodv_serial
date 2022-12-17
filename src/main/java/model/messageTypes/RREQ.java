package model.messageTypes;

import org.uncommons.maths.binary.BitString;

public class RREQ {

    private final BitString type = new BitString("000001");
    private BitString flags;
    private BitString hopCount;
    private BitString req;
    private BitString destAddr;
    private BitString destSeq;
    private BitString sourceAddr;
    private BitString sourceSeq;

    public RREQ(BitString flags, BitString hopCount, BitString req, BitString destAddr, BitString destSeq, BitString sourceAddr, BitString sourceSeq) {
        this.flags = flags;
        this.hopCount = hopCount;
        this.req = req;
        this.destAddr = destAddr;
        this.destSeq = destSeq;
        this.sourceAddr = sourceAddr;
        this.sourceSeq = sourceSeq;
    }

    public BitString getType() {
        return type;
    }

    public BitString getFlags() {
        return flags;
    }


    public BitString getHopCount() {
        return hopCount;
    }


    public BitString getReq() {
        return req;
    }

    public void setReq(BitString req) {
        this.req = req;
    }

    public BitString getDestAddr() {
        return destAddr;
    }


    public BitString getDestSeq() {
        return destSeq;
    }


    public BitString getSourceAddr() {
        return sourceAddr;
    }


    public BitString getSourceSeq() {
        return sourceSeq;
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
