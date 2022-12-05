// AT+SEND=128 = 128byte | Receive = bytes hexadecimal 80 -> not 80 bytes
package model.protocol;

import model.SerialManager;
import model.messageTypes.*;
import model.routing.ReverseRoutingEntry;
import model.routing.RoutingEntry;
import model.routing.RoutingTableManager;

import java.util.Base64;

public class ProtocolManager {

    // Node
    private final byte ownAddr = 1;
    // Local Network Data
    private byte localSeq = 0;
    private byte localReq = 0;
    // Network Parameters
    private int RREQRetries = 0;
    private static final int RREQ_RETRIES_MAX = 3;
    // Config Stuff
    private final RoutingTableManager routingTableManager = new RoutingTableManager();
    private final SerialManager serialManager = new SerialManager();

    /*
     * Init on start
     */
    public ProtocolManager() {
        init();
    }


    /*
     *
     * Utils / Init
     *
     */

    private void init() {
        addSelfToRoutingTable();
        // TODO config
    }

    // Add own address / route to routing table
    private void addSelfToRoutingTable() {
       routingTableManager.addRoutingEntry(ownAddr, (byte) 0, ownAddr, (byte) 0, null);
    }

    private void updateRoutingTable() {
        // TODO
    }

    private void updateReverseRoutingTable() {
        // TODO
    }

    // Check if route to destAddr exists
    private RoutingEntry findRoutingEntry(byte destAddr) {
        return routingTableManager.getRoutingTable().stream()
                .filter(route -> route.getDestAddr() == destAddr)
                .findAny()
                .orElse(null);
    }

    // Check if reverse routing entry exists
    private ReverseRoutingEntry findReverseRoutingEntry(byte destAddr) {
        return routingTableManager.getReverseRoutingTable().stream()
                .filter(route -> route.getDestAddr() == destAddr)
                .findAny()
                .orElse(null);
    }

    /*
     *
     * Sending Messages
     *
     */

    public void sendRREQBroadcast(RREQ routeRequest) {
        byte[] reqBytes = RREQtoBytes(routeRequest);
       // reqBytes = Base64.getEncoder().withoutPadding().encodeToString(reqBytes);
        String reqString = Base64.getEncoder().withoutPadding().encodeToString(reqBytes);
        serialManager.writeData(reqString);
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


    public void parseMessageType(String incomingMessage) {

        byte[] incomingMessageBytes;
        // TODO use real bytes and cases
        incomingMessageBytes = Base64.getDecoder().decode(incomingMessage);
        switch (incomingMessageBytes[0]) {
            case 1 -> processRREQ(incomingMessageBytes);
            case 2 -> processRREP(incomingMessageBytes);
            case 3 -> processRERR(incomingMessageBytes);
            case 4 -> processACK(incomingMessageBytes);
            // default -> Handle Error
        }
    }

    public void processRREQ(byte[] incomingMessageBytes) {
        // TODO: Parse relevant data
        byte sourceAddr = 0; // TODO: Get Source Address from the Lora LR,XXXX indicator
        byte reqID  = 0;
        byte destAddr = 0;


        // TODO: Check routing Tables if Route exists
        RoutingEntry routingEntry = findRoutingEntry(destAddr);
        if (routingEntry != null) {
            System.out.println("Route to Destination already exists");
        }
        // TODO: Update Routing Tables


        // TODO: Check if Addr is destination
        if (destAddr == ownAddr) {
            // TODO process message and send RREP
            System.out.println("Destination reached");
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
    public RREQ generateRREQ(byte destAddr) {
        localReq = (byte) (localReq + 1);
        localSeq = (byte) (localSeq + 1);
        return new RREQ((byte) 0, (byte) 0,localReq, destAddr, (byte) 0, ownAddr, localSeq);
    }

    private String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);

    }
    private byte[] decodeBase64(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    // Convert a RREQ to a byte array
    public byte[] RREQtoBytes(RREQ routeRequest) {
        return new byte[]{
                routeRequest.getType(),
                routeRequest.getFlags(),
                routeRequest.getHopCount(),
                routeRequest.getReq(),
                routeRequest.getDestAddr(),
                routeRequest.getDestSeq(),
                routeRequest.getSourceAddr(),
                routeRequest.getSourceSeq()
        };
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










}
