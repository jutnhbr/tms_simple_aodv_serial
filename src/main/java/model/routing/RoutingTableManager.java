package model.routing;

import java.util.ArrayList;
import java.util.List;

public class RoutingTableManager {
    private final List<RoutingEntry> routingTable = new ArrayList<>();
    private final List<ReverseRoutingEntry> reverseRoutingTable = new ArrayList<>();


    public void addRoutingEntry(byte destAddr, byte destSeqNum, byte nextHop, byte hopCount, List<Byte> preList) {
        routingTable.add(new RoutingEntry(destAddr, destSeqNum, nextHop, hopCount, preList));
    }
    public void addReverseRoutingEntry(byte destAddr, byte sourceAddr, byte hopCount, byte prev, byte req) {
        reverseRoutingTable.add(new ReverseRoutingEntry(destAddr, sourceAddr, hopCount, prev, req));
    }

    public List<RoutingEntry> getRoutingTable() {
        return routingTable;
    }
    public List<ReverseRoutingEntry> getReverseRoutingTable() {
        return reverseRoutingTable;
    }
}
