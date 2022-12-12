package model.routing;

public class ReverseRoutingEntry {

    private String destAddr;
    private String sourceAddr;
    private String hopCount;
    private String prev;
    private String req;


    public ReverseRoutingEntry(String destAddr, String sourceAddr, String hopCount, String prev, String req) {
        this.destAddr = destAddr;
        this.sourceAddr = sourceAddr;
        this.hopCount = hopCount;
        this.prev = prev;
        this.req = req;
    }


    public String getDestAddr() {
        return destAddr;
    }

    public void setDestAddr(String destAddr) {
        this.destAddr = destAddr;
    }

    public String getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }

    public String getHopCount() {
        return hopCount;
    }

    public void setHopCount(String hopCount) {
        this.hopCount = hopCount;
    }

    public String getPrev() {
        return prev;
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }

    public String getReq() {
        return req;
    }

    public void setReq(String req) {
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
