package model.routing;

public class ReverseRoutingEntry {

    private String destAddr;
    private String sourceAddr;
    private String hopCount;
    private String prev;


    public ReverseRoutingEntry(String destAddr, String sourceAddr, String hopCount, String prev) {
        this.destAddr = destAddr;
        this.sourceAddr = sourceAddr;
        this.hopCount = hopCount;
        this.prev = prev;
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


    @Override
    public String toString() {
        return "ReverseRoutingEntry{" +
                "destAddr=" + destAddr +
                ", sourceAddr=" + sourceAddr +
                ", hopCount=" + hopCount +
                ", prev=" + prev +
                '}';
    }
}
