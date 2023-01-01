package model.routing;

import java.util.HashSet;

public class ReverseRoutingEntry {

    private String destAddr;
    private String destSeqNum;
    private boolean validDestSeqNum;

    private boolean active;
    private String nextHop;
    private String hopCount;
    private String lifetime;
    private HashSet<String> precursors = new HashSet<>();


    public ReverseRoutingEntry(String destAddr, String destSeqNum, String nextHop, String hopCount, boolean validDestSeqNum, String lifetime) {
        this.destAddr = destAddr;
        this.destSeqNum = destSeqNum;
        this.nextHop = nextHop;
        this.hopCount = hopCount;
        this.validDestSeqNum = validDestSeqNum;
        this.lifetime = lifetime;
        this.active = false;
    }


    public String getDestAddr() {
        return destAddr;
    }

    public void setDestAddr(String destAddr) {
        this.destAddr = destAddr;
    }

    public String getDestSeqNum() {
        return destSeqNum;
    }

    public void setDestSeqNum(String destSeqNum) {
        this.destSeqNum = destSeqNum;
    }

    public boolean isValidDestSeqNum() {
        return validDestSeqNum;
    }

    public void setValidDestSeqNum(boolean validDestSeqNum) {
        this.validDestSeqNum = validDestSeqNum;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNextHop() {
        return nextHop;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }

    public String getHopCount() {
        return hopCount;
    }

    public void setHopCount(String hopCount) {
        this.hopCount = hopCount;
    }

    public String getLifetime() {
        return lifetime;
    }

    public void setLifetime(String lifetime) {
        this.lifetime = lifetime;
    }

    public HashSet<String> getPrecursors() {
        return precursors;
    }

    public void setPrecursors(HashSet<String> precursors) {
        this.precursors = precursors;
    }

    public void addPrecursor(String name){
        precursors.add(name);
    }

    @Override
    public String toString() {
        return "ReverseRoutingEntry{" +
                "destAddr='" + destAddr + '\'' +
                ", destSeqNum='" + destSeqNum + '\'' +
                ", validDestSeqNum=" + validDestSeqNum +
                ", active=" + active +
                ", nextHop='" + nextHop + '\'' +
                ", hopCount='" + hopCount + '\'' +
                ", lifetime='" + lifetime + '\'' +
                ", precursors=" + precursors +
                '}';
    }
}
