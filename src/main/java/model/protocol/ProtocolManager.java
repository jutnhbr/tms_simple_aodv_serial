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
    private int RREQRetries = 0;
    private final int NET_DIAMETER = 0;
    private final int RREQ_RETRIES_MAX = 2;
    private final int NODE_TRAVERSAL_TIME = 40;
    private final int NET_TRAVERSAL_TIME = 2 * NODE_TRAVERSAL_TIME * NET_DIAMETER;
    private final int PATH_DISCOVERY_TIME = 2 * NET_TRAVERSAL_TIME;
    private final int ACTIVE_ROUTE_TIMEOUT = 3000;
    private final int MY_ROUTE_TIMEOUT = 2 * ACTIVE_ROUTE_TIMEOUT;
    private boolean waitingForRREP = false;
    private long startTime;
    private RREQ bufferedRREQ;
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

    private void init() {
        addSelfToRoutingTable();
    }

    // Add own address / route to routing table
    private void addSelfToRoutingTable() {
        routingTableManager.addRoutingEntry(ownAddr, new BitString("00000000").toString(), ownAddr, new BitString("000000").toString(), null);
    }

    // Update Normal Routing Table
    private void addRoutingTable(String destAddr, String destSeqNum, String nextHop, String hopCount, String prev) {
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
    // Update Reverse Routing Table
    private void addReverseRoutingTable(String destAddr, String sourceAddr, String hopCount, String prevHop) {
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
        this.bufferedRREQ = routeRequest;
        // Generate binary string for RREQ
        BitString rreq = RREQtoBitString(routeRequest);

        console.printMessage("ProtocolManager >>> Sending RREQ: " + rreq.toString() + " with length " + rreq.getLength() + "bits.\n");
        // Encode RREQ
        String encodedRREQ = encodeBase64(rreq.toNumber().toByteArray());

        serialManager.writeData("AT+SEND=9");
        Thread.sleep(2000);
        serialManager.writeData(encodedRREQ);

        this.waitingForRREP = true;
        this.startTime = System.currentTimeMillis();
        waitForReply();
    }


    public void sendRREP(RREP routeReply) throws InterruptedException {
        BitString rrep = RREPtoBitString(routeReply);
        console.printMessage("ProtocolManager >>> Sending RREP: " + rrep.toString() + " with length " + rrep.getLength() + "bits.\n");
        String encodedRREP = encodeBase64(rrep.toNumber().toByteArray());
        serialManager.writeData("AT+SEND=9");
        Thread.sleep(2000);
        serialManager.writeData(encodedRREP);

    }

    public void sendDATA(DATA dataPacket) throws InterruptedException {
        BitString data = DATAtoBitString(dataPacket);
        console.printMessage("ProtocolManager >>> Sending DATA: " + data.toString() + " with length " + data.getLength() + "bits.\n");
        String encodedDATA = encodeBase64(data.toNumber().toByteArray());
        serialManager.writeData("AT+SEND=" + encodedDATA.length());
        Thread.sleep(2000);
        serialManager.writeData(encodedDATA);
    }


    private void waitForReply() throws InterruptedException {
        if(waitingForRREP && RREQRetries < RREQ_RETRIES_MAX && (System.currentTimeMillis() - startTime) < NET_TRAVERSAL_TIME) {
            console.printMessage("ProtocolManager >>> Waiting for RREP...");
            waitForReply();
        } else if (RREQRetries < RREQ_RETRIES_MAX) {
            console.printMessage("ProtocolManager >>> RREP not received, retrying...");
            // TODO back off timer
            this.startTime = System.currentTimeMillis();
            RREQRetries++;
            this.localReq++;
            this.bufferedRREQ.setReq(new BitString(String.format("%6s", Integer.toBinaryString(localReq)).replace(' ', '0')));
            sendRREQBroadcast(bufferedRREQ);
        } else {
            console.printMessage("ProtocolManager >>> RREP not received, giving up...");
            RREQRetries = 0;
            waitingForRREP = false;
            bufferedRREQ = null;
        }

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
            case 0 -> {
                console.printMessage("ProtocolManager >>> Payload parsed as DATA.\n");
                processDATA(incomingMessage);
            }
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

        // TODO Check if we know the RREQ already

        // Check routing Tables if Route exists
        RoutingEntry routingEntry = findRoutingEntry(destAddr);
        if (routingEntry != null) {
            console.printMessage("ProtocolManager >>> Route to " + destAddr + " already exists.\n");
        } else {
            addRoutingTable(destAddr, destSeqNum, null, hopCount, this.prevHop);
        }

        // Check reverse routing table if route exists
        ReverseRoutingEntry reverseRoutingEntry = findReverseRoutingEntry(sourceAddr);
        if (reverseRoutingEntry != null) {
            console.printMessage("ProtocolManager >>> Reverse Route from " + sourceAddr + " already exists.\n");
            int pos = routingTableManager.getReverseRoutingTable().indexOf(reverseRoutingEntry);
            // TODO: Update seq number
            // routingTableManager.getReverseRoutingTable().get(pos).setSeq;
        } else {
            addReverseRoutingTable(sourceAddr, sourceSeqNum, reqID, this.prevHop);
        }


        if (destAddr.equals(ownAddr)) {
            console.printMessage("ProtocolManager >>> RREQ for own address received. Sending RREP.\n");
            // Hop Count to 0
            BitString hop = new BitString("00000000");

            // TODO: Replace with MY_ROUTE_TIMEOUT
            BitString lifetime = new BitString("0000000000000000");

            BitString destSeq = incrementFrame(destSeqNum, "%8s");
            String localSeqAsBinary = String.format("%8s", Integer.toBinaryString(localSeqNum)).replace(' ', '0');
            BitString localSeq = new BitString(localSeqAsBinary);
            if(!destSeqNum.equals(localSeqAsBinary)) {
                destSeq = localSeq;
            }
            // Generate new RREP
            RREP rrep = new RREP(lifetime, new BitString(sourceAddr), destSeq, new BitString(ownAddrAsBinary()), hop);
            // Send RREP
            sendRREP(rrep);

        } else {
            console.printMessage("ProtocolManager >>> RREQ for other address received. Forwarding...\n");;
            // Increment HopCount
            BitString newHopCount = incrementFrame(hopCount, "%6s");
            // Broadcast updated RREQ
            RREQ newRREQ = new RREQ(
                    new BitString("000000"),
                    newHopCount,
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
        String destinationAddress = incomingMessage.substring(incomingMessage.length() - 48, incomingMessage.length() - 32);
        String destinationSequence = incomingMessage.substring(incomingMessage.length() - 32, incomingMessage.length() - 24);
        String originatorAddress = incomingMessage.substring(incomingMessage.length() - 24, incomingMessage.length() - 8);
        String hopCount = incomingMessage.substring(incomingMessage.length() - 8);

        RREP rrep = new RREP(new BitString(lifeTime),
                        new BitString(destinationAddress),
                        new BitString(destinationSequence),
                        new BitString(hopCount),
                        new BitString(originatorAddress));

        if (destinationAddress.equals(ownAddr)) {
            console.printMessage("ProtocolManager >>> RREP for own address received.\n");
            routingTableManager.addRoutingEntry(originatorAddress, destinationSequence, addrFrom, hopCount, "EMPTY");
        } else {
            console.printMessage("ProtocolManager >>> RREP for other address received. Forwarding...\n");
            // Increment HopCount
            rrep.setHopCount(incrementFrame(hopCount, "%6s"));
        }
    }

   private void processDATA(String incomingMessage) {
        console.printMessage("ProtocolManager >>> DATA received:" + incomingMessage +"\n");
   }


    /*
     *
     * Message Decoding and Encoding
     *
     */

    // Create RREQ Message with a specific destination address
    public void generateRREQ(String destAddr) throws InterruptedException {

        // Check if Route already exists
        RoutingEntry routingEntry = findRoutingEntry(destAddr);
        if (routingEntry != null) {
            console.printMessage("ProtocolManager >>> Route to " + destAddr + " already exists.\n");
            return;
        }

        // Increment Req ID and Seq Number
        localSeqNum = localSeqNum + 1;
        localReq = localReq + 1;
        // Set first flag (unknown sequence number)
        BitString flags = new BitString("100000");
        // Convert to BitStrings and generate RREQ
        String localSeqAsBinary = localSeqAsBinary();
        String localReqAsBinary = localReqAsBinary();
        String destAddrBinary = String.format("%16s", Integer.toBinaryString(Integer.parseInt(destAddr, 16))).replace(' ', '0');
        String ownAddrBinary = ownAddrAsBinary();
        RREQ req = new RREQ(
                // Flags
                flags,
                // Hop Count set to 0
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
        sendRREQBroadcast(req);
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

    private BitString DATAtoBitString(DATA dataPacket) {
        return new BitString(dataPacket.getType().toString() + dataPacket.getMessage());
    }

    private String localReqAsBinary() {
        return String.format("%6s", Integer.toBinaryString(localReq)).replace(' ', '0');
    }
    private String localSeqAsBinary() {
        return String.format("%8s", Integer.toBinaryString(localSeqNum)).replace(' ', '0');
    }
    private String ownAddrAsBinary() {
        return String.format("%16s", Integer.toBinaryString(Integer.parseInt(ownAddr, 16))).replace(' ', '0');
    }


    public BitString incrementFrame(String frame, String format) {
        BitString frameBitString = new BitString(frame);
        int frameCount = Integer.parseInt(frameBitString.toString(), 2);
        frameCount++;
        return new BitString(String.format(format, Integer.toBinaryString(frameCount)).replace(' ', '0'));
    }



    public RoutingTableManager getRoutingTableManager() {
        return routingTableManager;
    }

}
