package model.routing;

import java.util.ArrayList;
import java.util.List;

public class RoutingEntry {

    private String destAddr;
    private String destSeqNum;
    private String nextHop;
    private String hopCount;
    private String prev;


    public RoutingEntry(String destAddr, String destSeqNum, String nextHop, String hopCount, String prev) {
        this.destAddr = destAddr;
        this.destSeqNum = destSeqNum;
        this.nextHop = nextHop;
        this.hopCount = hopCount;
        this.prev = prev;
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

    public String getPreList() {
        return prev;
    }

    public void setPreList(String prev) {
        this.prev = prev;
    }


    @Override
    public String toString() {
        return "RoutingEntry{" +
                "destAddr=" + destAddr +
                ", destSeqNum=" + Integer.parseInt(destSeqNum,2) +
                ", nextHop=" + nextHop +
                ", hopCount=" + Integer.parseInt(hopCount,2) +
                ", prev=" + prev +
                '}';
    }
}
