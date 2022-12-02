package model.routing;

public class ReverseRoutingEntry {

    private byte destAddr;
    private byte sourceAddr;
    private byte hopCount;
    private byte prev;
    private byte req;


    public ReverseRoutingEntry(byte destAddr, byte sourceAddr, byte hopCount, byte prev, byte req) {
        this.destAddr = destAddr;
        this.sourceAddr = sourceAddr;
        this.hopCount = hopCount;
        this.prev = prev;
        this.req = req;
    }


    public byte getDestAddr() {
        return destAddr;
    }

    public void setDestAddr(byte destAddr) {
        this.destAddr = destAddr;
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

    public byte getPrev() {
        return prev;
    }

    public void setPrev(byte prev) {
        this.prev = prev;
    }

    public byte getReq() {
        return req;
    }

    public void setReq(byte req) {
        this.req = req;
    }

    @Override
    public String toString() {
        return "ReverseRoutingEntry{" +
                "destAddr=" + destAddr +
                ", sourceAddr=" + sourceAddr +
                ", hopCount=" + hopCount +
                ", prev=" + prev +
                ", req=" + req +
                '}';
    }
}
