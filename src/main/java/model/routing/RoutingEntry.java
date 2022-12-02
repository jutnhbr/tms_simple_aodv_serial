package model.routing;

import java.util.ArrayList;
import java.util.List;

public class RoutingEntry {

    private byte destAddr;
    private byte destSeqNum;
    private byte nextHop;
    private byte hopCount;
    private List<Byte> preList = new ArrayList<>();


    public RoutingEntry(byte destAddr, byte destSeqNum, byte nextHop, byte hopCount, List<Byte> preList) {
        this.destAddr = destAddr;
        this.destSeqNum = destSeqNum;
        this.nextHop = nextHop;
        this.hopCount = hopCount;
        this.preList = preList;
    }

    public byte getDestAddr() {
        return destAddr;
    }

    public void setDestAddr(byte destAddr) {
        this.destAddr = destAddr;
    }

    public byte getDestSeqNum() {
        return destSeqNum;
    }

    public void setDestSeqNum(byte destSeqNum) {
        this.destSeqNum = destSeqNum;
    }

    public byte getNextHop() {
        return nextHop;
    }

    public void setNextHop(byte nextHop) {
        this.nextHop = nextHop;
    }

    public byte getHopCount() {
        return hopCount;
    }

    public void setHopCount(byte hopCount) {
        this.hopCount = hopCount;
    }

    public List<Byte> getPreList() {
        return preList;
    }

    public void setPreList(List<Byte> preList) {
        this.preList = preList;
    }


    @Override
    public String toString() {
        return "RoutingEntry{" +
                "destAddr=" + destAddr +
                ", destSeqNum=" + destSeqNum +
                ", nextHop=" + nextHop +
                ", hopCount=" + hopCount +
                ", preList=" + preList +
                '}';
    }
}
