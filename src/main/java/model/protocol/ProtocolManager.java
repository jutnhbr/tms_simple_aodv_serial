// AT+SEND=128 = 128byte | Receive = bytes hexadecimal 80 -> not 80 bytes
package model.protocol;

import model.SerialManager;
import model.messageTypes.*;
import model.routing.ReverseRoutingEntry;
import model.routing.RoutingEntry;
import model.routing.RoutingTableManager;
import java.util.Arrays;
import java.util.Base64;

public class ProtocolManager {

    // Node
    private final byte ownAddr = Byte.parseByte("AAAA", 16);
    // Local Network Data
    private byte localSeq = 0;
    private byte localReq = 0;
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
        reqBytes = Base64.getEncoder().withoutPadding().encodeToString(reqBytes).getBytes();
        serialManager.writeData(Arrays.toString(reqBytes));
    }

    public void sendRREP(RREP routeReply) {
        // TODO

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
        incomingMessageBytes = Base64.getDecoder().decode(incomingMessage.substring(0, 8));
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
        byte sourceAddr = 0;
        byte reqID  = 0;

        // TODO: Update Routing Tables

        // TODO: Check routing Tables if Route exists

        // TODO: Check if Addr is destination

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

    private byte encodeBase64(byte[] bytes) {
        return 0;
    }
    private byte[] decodeBase64(byte b) {
        return null;
    }

    private byte[] RREQtoBytes(RREQ routeRequest) {
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










}
