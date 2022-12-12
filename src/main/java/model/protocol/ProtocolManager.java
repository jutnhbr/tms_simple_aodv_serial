// AT+SEND=128 = 128byte | Receive = bytes hexadecimal 80 -> not 80 bytes
package model.protocol;

import model.SerialManager;
import model.messageTypes.*;
import model.routing.ReverseRoutingEntry;
import model.routing.RoutingEntry;
import model.routing.RoutingTableManager;
import org.uncommons.maths.binary.BitString;
import view.Console;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

public class ProtocolManager {

    // Node
    private final String ownAddr = "ADDF";
    // Local Network Data
    private int localSeqNum = 0;
    private int localReq = 0;
    // Network Parameters
    private int RREQRetries = 0;
    private static final int RREQ_RETRIES_MAX = 3;
    // Config Stuff
    private final RoutingTableManager routingTableManager = new RoutingTableManager();
    private final SerialManager serialManager;
    private final Console console = new Console();

    /*
     * Init on start
     */
    public ProtocolManager(SerialManager serialManager) {
        this.serialManager = serialManager;
        init();
    }

    /*
     *
     * Utils / Init
     *
     */

    private void init() {
        addSelfToRoutingTable();
    }

    // Add own address / route to routing table
    private void addSelfToRoutingTable() {
        routingTableManager.addRoutingEntry(ownAddr, new BitString("00000000").toString(), ownAddr, new BitString("000000").toString(), null);
    }

    private void updateRoutingTable(String destAddr, String destSeqNum, String nextHop, String hopCount, List<String> preList) {
        // TODO: Get PreList from RoutingTable
        // TODO: Get nextHop
        RoutingEntry entry = new RoutingEntry(destAddr, destSeqNum, nextHop, hopCount, preList);
        // Re-Check if entry already exists
        if (routingTableManager.getRoutingTable().contains(entry)) {
            console.printMessage("ProtocolManager >>> Routing Table already contains entry: " + entry);
        } else {
            // Add entry
            console.printMessage("ProtocolManager >>> Adding entry to routing table: " + entry);
            routingTableManager.addRoutingEntry(destAddr, destSeqNum, nextHop, hopCount, preList);
        }

    }

    private void updateReverseRoutingTable() {
        // TODO
    }

    // Check if route to destAddr exists
    private RoutingEntry findRoutingEntry(String destAddr) {
        return routingTableManager.getRoutingTable().stream()
                .filter(route -> Objects.equals(route.getDestAddr(), destAddr))
                .findAny()
                .orElse(null);
    }

    // Check if reverse routing entry exists
    private ReverseRoutingEntry findReverseRoutingEntry(String destAddr) {
        return routingTableManager.getReverseRoutingTable().stream()
                .filter(route -> Objects.equals(route.getDestAddr(), destAddr))
                .findAny()
                .orElse(null);
    }

    /*
     *
     * Sending Messages
     *
     */

    public void sendRREQBroadcast(RREQ routeRequest) {
        BitString rreq = RREQtoBitString(routeRequest);
        console.printMessage("ProtocolManager >>> Sending RREQ: " + rreq.toString() + " with length " + rreq.getLength() + "bits.\n");
        String encodedRREQ = encodeBase64(rreq.toNumber().toByteArray());
        serialManager.writeData(encodedRREQ);
    }

    public void sendRREP(RREP routeReply) {
        byte[] repBytes = RREPtoBytes(routeReply);
        String repString = Base64.getEncoder().withoutPadding().encodeToString(repBytes);
        serialManager.writeData(repString);

    }

    public void sendRERR(RERR routeError) {

    }

    public void sendACK(ACK acknowledgement) {

    }

    public void sendData(DATA data) {

    }

    /*
     *
     * Parsing and Processing Messages
     *
     */

    public void receiveIncomingPayload(byte[] payload) {
        // TODO Receive Bytes from SerialManager / Queue
        // TODO: Decode Base64
        // TODO Convert to binaryString
        String binaryData = null;
        parseMessageType(binaryData);
    }


    public void parseMessageType(String incomingMessage) {
        String msgType = incomingMessage.substring(0, incomingMessage.length() - 66);
        switch (Integer.parseInt(msgType, 2)) {
            case 1 -> {
                console.printMessage("ProtocolManager >>> Received RREQ.\n");
                processRREQ(incomingMessage);
            }
            case 2 -> console.printMessage("ProtocolManager >>> Received RREP.\n");
            case 3 -> console.printMessage("ProtocolManager >>> Received ACK.\n");
            case 4 -> console.printMessage("ProtocolManager >>> Received RERR.\n");
            case 5 -> console.printMessage("ProtocolManager >>> Received DATA.\n");
            default -> console.printErrMessage("ProtocolManager >>> Received unknown message type: " + msgType);
        }
    }

    public void processRREQ(String incomingMessage) {
        // Parse relevant data
        String sourceAddr = incomingMessage.substring(incomingMessage.length() - 24, incomingMessage.length() - 8);
        String destAddr = incomingMessage.substring(incomingMessage.length() - 48, incomingMessage.length() - 32);
        String reqID = incomingMessage.substring(incomingMessage.length() - 54, incomingMessage.length() - 48);
        String destSeqNum = incomingMessage.substring(incomingMessage.length() - 32, incomingMessage.length() - 24);
        String hopCount = incomingMessage.substring(incomingMessage.length() - 60, incomingMessage.length() - 54);

        // Check routing Tables if Route exists
        RoutingEntry routingEntry = findRoutingEntry(destAddr);
        if (routingEntry != null) {
            console.printMessage("ProtocolManager >>> Route to " + destAddr + " already exists.\n");
        } else {
            // TODO nextHop and preList
            updateRoutingTable(destAddr, destSeqNum, null, hopCount, null);

        }

        if (destAddr.equals(ownAddr)) {
            console.printMessage("ProtocolManager >>> RREQ for own address received. Sending RREP.\n");
            // TODO process message and send RREP
        }

        // TODO: Send RREP or Forward RREQ
    }


    public void processRREP(byte[] incomingMessageBytes) {

    }

    public void processRERR(byte[] incomingMessageBytes) {
        // TODO
    }

    public void processACK(byte[] incomingMessageBytes) {
        // TODO
    }


    /*
     *
     * Message Decoding and Encoding
     *
     */

    // Create RREQ Message with a specific destination address
    public RREQ generateRREQ(String destAddr) {
        // Increment Req ID and Seq Number
        localSeqNum = localSeqNum + 1;
        localReq = localReq + 1;
        // Convert to BitStrings and generate RREQ
        String localSeqAsBinary = String.format("%8s", Integer.toBinaryString(localSeqNum)).replace(' ', '0');
        String localReqAsBinary = String.format("%6s", Integer.toBinaryString(localReq)).replace(' ', '0');
        String destAddrBinary = String.format("%16s", Integer.toBinaryString(Integer.parseInt(destAddr, 16))).replace(' ', '0');
        String ownAddrBinary = String.format("%16s", Integer.toBinaryString(Integer.parseInt(ownAddr, 16))).replace(' ', '0');
        return new RREQ(
                new BitString("000000"),
                new BitString("000000"),
                new BitString(localReqAsBinary),
                new BitString(destAddrBinary),
                new BitString("00000000"),
                new BitString(ownAddrBinary),
                new BitString(localSeqAsBinary)
        );
    }

    private String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);

    }
    private byte[] decodeBase64(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    // Convert a RREQ to a byte array
    public BitString RREQtoBitString(RREQ routeRequest) {
        return new BitString(
                routeRequest.getType().toString() +
                        routeRequest.getFlags() +
                        routeRequest.getHopCount() +
                        routeRequest.getReq() +
                        routeRequest.getDestAddr() +
                        routeRequest.getDestSeq() +
                        routeRequest.getSourceAddr() +
                        routeRequest.getSourceSeq()
        );
    }

    public byte[] RREPtoBytes(RREP routeReply) {
        return new byte[]{
                routeReply.getType(),
                routeReply.getDestAddr(),
                routeReply.getDestSeq(),
                routeReply.getHopCount(),
                routeReply.getSourceAddr(),

        };
    }

    public RoutingTableManager getRoutingTableManager() {
        return routingTableManager;
    }

    @Override
    public String toString() {
        return "ProtocolManager{" +
                "ownAddr='" + ownAddr + '\'' +
                ", localSeqNum=" + localSeqNum +
                ", localReq=" + localReq +
                ", RREQRetries=" + RREQRetries +
                ", routingTableManager=" + routingTableManager +
                ", serialManager=" + serialManager +
                ", console=" + console +
                '}';
    }
}
