// AT+SEND=128 = 128byte | Receive = bytes hexadecimal 80 -> not 80 bytes
package model.protocol;

import model.SerialManager;
import model.messageTypes.*;
import model.routing.ReverseRoutingEntry;
import model.routing.RoutingEntry;
import model.routing.RoutingTableManager;
import org.uncommons.maths.binary.BitString;
import threads.ReadingThread;
import view.Console;

import java.math.BigInteger;
import java.util.Base64;
import java.util.Objects;

public class ProtocolManager {

    // Node
    private final String ownAddr = "ADDF";
    // Local Network Data
    private int localSeqNum = 0;
    private int localReq = 0;
    private String prevHop;
    // Network Parameters
    //private int RREQRetries = 0;
    private static final int RREQ_RETRIES_MAX = 3;
    // Config Stuff
    private final RoutingTableManager routingTableManager = new RoutingTableManager();
    private final ReadingThread readingThread;
    private final SerialManager serialManager;
    private final Console console = new Console();

    /*
     * Init on start
     */
    public ProtocolManager(SerialManager serialManager, ReadingThread readingThread) {
        this.serialManager = serialManager;
        this.readingThread = readingThread;
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

    private void updateRoutingTable(String destAddr, String destSeqNum, String nextHop, String hopCount, String prev) {
        RoutingEntry entry = new RoutingEntry(destAddr, destSeqNum, nextHop, hopCount, prev);
        // Re-Check if entry already exists
        if (routingTableManager.getRoutingTable().contains(entry)) {
            console.printMessage("ProtocolManager >>> Routing Table already contains entry: " + entry);
        } else {
            // Add entry
            console.printMessage("ProtocolManager >>> Adding entry to routing table: " + entry);
            routingTableManager.addRoutingEntry(destAddr, destSeqNum, nextHop, hopCount, prev);
        }

    }

    private void updateReverseRoutingTable(String destAddr, String sourceAddr, String hopCount, String prevHop) {
        ReverseRoutingEntry entry = new ReverseRoutingEntry(destAddr, sourceAddr, hopCount, prevHop);
        // Re-Check if entry already exists
        if (routingTableManager.getReverseRoutingTable().contains(entry)) {
            console.printMessage("ProtocolManager >>> Reverse Routing Table already contains entry: " + entry);
        } else {
            // Add entry
            console.printMessage("ProtocolManager >>> Adding entry to reverse routing table: " + entry);
            routingTableManager.addReverseRoutingEntry(destAddr, sourceAddr, hopCount, prevHop);
        }
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

    public void sendRREQBroadcast(RREQ routeRequest) throws InterruptedException {
        BitString rreq = RREQtoBitString(routeRequest);
        console.printMessage("ProtocolManager >>> Sending RREQ: " + rreq.toString() + " with length " + rreq.getLength() + "bits.\n");
        String encodedRREQ = encodeBase64(rreq.toNumber().toByteArray());
        serialManager.writeData("AT+SEND=9");
        Thread.sleep(2000);
        serialManager.writeData(encodedRREQ);
    }

    public void sendRREP(RREP routeReply) throws InterruptedException {
        BitString rrep = RREPtoBitString(routeReply);
        console.printMessage("ProtocolManager >>> Sending RREP: " + rrep.toString() + " with length " + rrep.getLength() + "bits.\n");
        String encodedRREP = encodeBase64(rrep.toNumber().toByteArray());
        serialManager.writeData("AT+SEND=9");
        Thread.sleep(2000);
        serialManager.writeData(encodedRREP);


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


    public void receiveIncomingPayload() throws InterruptedException {
        String payload;

        while (true) {
            if (readingThread.getCommandQueue().peek() != null) {
                payload = readingThread.getCommandQueue().poll();
                // Split LR,XXXX,XX, from Payload
                assert payload != null;
                String[] payloadSplit = payload.split(",");

                // Check if payload contains all necessary data
                if (payloadSplit.length < 4) {
                    console.printMessage("ProtocolManager >>> Received invalid payload: " + payload + "\n");
                    return;
                }
                // Save [1] XXXX as prevHop
                this.prevHop = payloadSplit[1];
                // Save [3] the rest as payload
                String payloadString = payloadSplit[3];
                payloadString = payloadString.replace("\n", "").replace("\r", "");
                console.printMessage("ProtocolManager >>> Received payload: " + payloadString + "\n");
                byte[] payloadBytes = decodeBase64(payloadString);
                String binaryData = new BigInteger(1, payloadBytes).toString(2);
                parseMessageType(binaryData);
            } else {
                Thread.sleep(2000);
            }
        }
    }


    public void parseMessageType(String incomingMessage) throws InterruptedException {
        String msgType = incomingMessage.substring(0, incomingMessage.length() - 66);
        switch (Integer.parseInt(msgType, 2)) {
            case 1 -> {
                console.printMessage("ProtocolManager >>> Payload parsed as RREQ.\n");
                processRREQ(incomingMessage);
            }
            case 2 -> {
                console.printMessage("ProtocolManager >>> Payload parsed as RREP.\n");
                processRREP(incomingMessage, this.prevHop);
            }
            case 3 -> console.printMessage("ProtocolManager >>> Payload parsed as ACK.\n");
            case 4 -> console.printMessage("ProtocolManager >>> Payload parsed as RERR.\n");
            case 5 -> console.printMessage("ProtocolManager >>> Payload parsed as DATA.\n");
            default -> console.printErrMessage("ProtocolManager >>> Received unknown message type: " + msgType);
        }
    }

    public void processRREQ(String incomingMessage) throws InterruptedException {
        // Parse relevant data
        String sourceAddr = incomingMessage.substring(incomingMessage.length() - 24, incomingMessage.length() - 8);
        String sourceSeqNum = incomingMessage.substring(incomingMessage.length() - 8);
        String destAddr = incomingMessage.substring(incomingMessage.length() - 48, incomingMessage.length() - 32);
        String reqID = incomingMessage.substring(incomingMessage.length() - 54, incomingMessage.length() - 48);
        String destSeqNum = incomingMessage.substring(incomingMessage.length() - 32, incomingMessage.length() - 24);
        String hopCount = incomingMessage.substring(incomingMessage.length() - 60, incomingMessage.length() - 54);

        // Check routing Tables if Route exists
        RoutingEntry routingEntry = findRoutingEntry(destAddr);
        if (routingEntry != null) {
            console.printMessage("ProtocolManager >>> Route to " + destAddr + " already exists.\n");
        } else {
            updateRoutingTable(destAddr, destSeqNum, null, hopCount, this.prevHop);

        }

        if (destAddr.equals(ownAddr)) {
            console.printMessage("ProtocolManager >>> RREQ for own address received. Sending RREP.\n");

            // RREP reply = new RREP();

            // TODO process message
            // TODO: Generate new RREP
            // TODO: Send RREP
        } else {
            console.printMessage("ProtocolManager >>> RREQ for other address received. Forwarding...\n");
            // Add to Reverse Routing Table
            updateReverseRoutingTable(destAddr, sourceAddr, hopCount, this.prevHop);
            // Update HopCount
            int hopCountInt = Integer.parseInt(hopCount, 2);
            hopCountInt++;
            hopCount = String.format("%06d", Integer.parseInt(Integer.toBinaryString(hopCountInt)));
            // Broadcast updated RREQ
            RREQ newRREQ = new RREQ(
                    new BitString("000000"),
                    new BitString(hopCount),
                    new BitString(reqID),
                    new BitString(destAddr),
                    new BitString(destSeqNum),
                    new BitString(sourceAddr),
                    new BitString(sourceSeqNum)
            );
            sendRREQBroadcast(newRREQ);
        }
    }


    public void processRREP(String incomingMessage, String addrFrom) {

        String lifeTime = incomingMessage.substring(incomingMessage.length() - 66, incomingMessage.length() - 48);
        String destinationAdress = incomingMessage.substring(incomingMessage.length() - 48, incomingMessage.length() - 32);
        String destinationSequence = incomingMessage.substring(incomingMessage.length() - 32, incomingMessage.length() - 24);
        String originatorAdress = incomingMessage.substring(incomingMessage.length() - 24, incomingMessage.length() - 8);
        String hopCount = incomingMessage.substring(incomingMessage.length() - 8);

        RREP rrep = new RREP(new BitString(lifeTime),
                        new BitString(destinationAdress),
                        new BitString(destinationSequence),
                        new BitString(hopCount),
                        new BitString(originatorAdress));

        if (destinationAdress.equals(ownAddr)) {
            console.printMessage("ProtocolManager >>> RREP for own address received.\n");
            routingTableManager.addRoutingEntry(originatorAdress, destinationSequence, addrFrom, hopCount, "EMPTY");
        } else {
            console.printMessage("ProtocolManager >>> RREP for other address received. Forwarding...\n");
            BitString bitString = new BitString(hopCount);
            // parse to int
            int type__ = Integer.parseInt(bitString.toString(), 2);
            type__ = type__ + 1;
            // Convert to binary
            String binary = String.format("%6s", Integer.toBinaryString(type__)).replace(' ', '0');
            bitString = new BitString(binary);
            rrep.setHopCount(bitString);
        }
    }

    public void processRERR(byte[] incomingMessageBytes) {
    }

    public void processACK(byte[] incomingMessageBytes) {
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
                // Flags
                new BitString("000000"),
                // Hop Count
                new BitString("000000"),
                // Req ID
                new BitString(localReqAsBinary),
                // Dest Addr
                new BitString(destAddrBinary),
                // Dest Seq
                new BitString("00000000"),
                // Origin Addr
                new BitString(ownAddrBinary),
                // Origin Seq
                new BitString(localSeqAsBinary)
        );
    }

    private String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);

    }

    private byte[] decodeBase64(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    // Convert a RREQ to BitString
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

    public BitString RREPtoBitString(RREP routeReply) {
        return new BitString(
                routeReply.getType().toString() +
                        routeReply.getDestAddr() +
                        routeReply.getDestSeq() +
                        routeReply.getHopCount() +
                        routeReply.getSourceAddr()

        );
    }

    public RoutingTableManager getRoutingTableManager() {
        return routingTableManager;
    }

}
