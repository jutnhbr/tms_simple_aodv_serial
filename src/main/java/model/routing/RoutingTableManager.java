package model.routing;

import java.util.ArrayList;
import java.util.List;

public class RoutingTableManager {
    private final List<RoutingEntry> routingTable = new ArrayList<>();
    private final List<ReverseRoutingEntry> reverseRoutingTable = new ArrayList<>();


//    public void addRoutingEntry(String destAddr, String destSeqNum, String nextHop, String hopCount, String prev) {
//        routingTable.add(new RoutingEntry(destAddr, destSeqNum, nextHop, hopCount, prev));
//    }

    public void addRoutingEntry(RoutingEntry routingEntry){
        routingTable.add(routingEntry);
    }
    public void addReverseRoutingEntry(String destAddr, String destSeqNum, String nextHop, String hopCount, boolean validDestSeqNum, String lifetime) {
        reverseRoutingTable.add(new ReverseRoutingEntry(destAddr, destSeqNum,nextHop,hopCount,validDestSeqNum,lifetime));
    }

    public List<RoutingEntry> getRoutingTable() {
        return routingTable;
    }
    public List<ReverseRoutingEntry> getReverseRoutingTable() {
        return reverseRoutingTable;
    }
}
