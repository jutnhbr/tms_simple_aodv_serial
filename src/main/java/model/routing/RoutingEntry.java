package model.routing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class RoutingEntry {

    private String destAddr;
    private String destSeqNum;
    private boolean validDestSeqNum;

    private boolean active;
    private String nextHop;
    private String hopCount;
    private String lifetime;
    private HashSet<String> precursors = new HashSet<>();


    public RoutingEntry(String destAddr, String destSeqNum, String nextHop, String hopCount, boolean validDestSeqNum, String lifetime) {
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

    public boolean isValidDestSeqNum() {
        return validDestSeqNum;
    }

    public void setValidDestSeqNum(boolean validDestSeqNum) {
        this.validDestSeqNum = validDestSeqNum;
    }

    public String getLifetime() {
        return lifetime;
    }

    public void setLifetime(String lifetime) {
        this.lifetime = lifetime;
    }
public void addPrecursor(String name){
        precursors.add(name);
}
    public HashSet<String> getPrecursors() {
        return precursors;
    }

    public void setPrecursors(HashSet<String> precursors) {
        this.precursors = precursors;
    }

    @Override
    public String toString() {
        return "RoutingEntry{" +
                "destAddr=" + destAddr +
                ", destSeqNum=" + Integer.parseInt(destSeqNum,2) +
                ", validDestSeqNum=" + validDestSeqNum +
                ", nextHop=" + nextHop +
                ", hopCount=" + Integer.parseInt(hopCount,2) +
                ", prev=" + precursors +
                ", lifeTime=" + lifetime +
                '}';
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
