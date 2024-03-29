package model.protocol;

import model.SerialManager;
import model.messageTypes.*;
import model.routing.ReverseRoutingEntry;
import model.routing.RoutingEntry;
import model.routing.RoutingTableManager;
import org.apache.commons.lang3.ArrayUtils;
import org.uncommons.maths.binary.BitString;
import view.Console;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

// 0007 MobaNet12
public class ProtocolManager {

    // Node Information
    private final String ownAddr = "13";
    // Local Network Data
    private int localSeqNum = 0;
    private int localReq = 0;
    private String prevHop;
    // Network Parameters
    private int RREQRetries = 0;
    private final int RREQ_RETRIES_MAX = 2;
    private final int NET_DIAMETER = 35;
    private final int NODE_TRAVERSAL_TIME = 40;
    private final int NET_TRAVERSAL_TIME = 2 * NODE_TRAVERSAL_TIME * NET_DIAMETER;
    private final int PATH_DISCOVERY_TIME = 2 * NET_TRAVERSAL_TIME;
    private final int ACTIVE_ROUTE_TIMEOUT = 3000;
    private final int MY_ROUTE_TIMEOUT = 2 * ACTIVE_ROUTE_TIMEOUT;
    private boolean waitingForRREP = false;
    private int currentWaitingTime = NET_TRAVERSAL_TIME;
    private long startTime;
    // Buffered RREQ Packet for Retries
    private RREQ bufferedRREQ;
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

    private void init() {
        addSelfToRoutingTable();
    }

    // Add own address / route to routing table on start up
    private void addSelfToRoutingTable() {
        String ownLifeTime = String.valueOf(System.currentTimeMillis() + 2 * NET_TRAVERSAL_TIME - 2L * Integer.parseInt("0") * NET_TRAVERSAL_TIME);
        RoutingEntry entry = new RoutingEntry(ownAddr, new BitString("00000000").toString(), ownAddr, new BitString("000000").toString(), true, ownLifeTime);
        routingTableManager.addRoutingEntry(entry);
    }

    // Update Normal Routing Table
    private void addRoutingTable(String destAddr, String destSeqNum, String nextHop, String hopCount, boolean validDestSeq, String lifetime, String prev) {

        // Re-Calculate Lifetime
        lifetime = String.valueOf(System.currentTimeMillis() + 2 * NET_TRAVERSAL_TIME - 2L * Integer.parseInt(hopCount) * NET_TRAVERSAL_TIME);

        RoutingEntry entry = new RoutingEntry(destAddr, destSeqNum, nextHop, hopCount, validDestSeq, lifetime);
        // Add Precursor to Routing Entry Precursor List
        if (prev != null) {
            entry.addPrecursor(prev);
        }
        console.printMessage("ProtocolManager >>> Adding entry to routing table: " + entry + "\n");
        routingTableManager.addRoutingEntry(entry);

    }

    // Update Reverse Routing Table
    private void addReverseRoutingTable(String destAddr, String destSeqNum, String nextHop, String hopCount, boolean validDestSeqNum, String lifetime, String prev) {
        ReverseRoutingEntry entry = new ReverseRoutingEntry(destAddr, destSeqNum, nextHop, hopCount, validDestSeqNum, lifetime);

        // Add Precursor to Reverse Routing Entry Precursor List
        if (prev != null) {
            entry.addPrecursor(prev);
        }
        if (routingTableManager.getReverseRoutingTable().contains(entry)) {
            console.printMessage("ProtocolManager >>> Reverse Routing Table already contains entry: " + entry + "\n");
        } else {
            // Add entry
            console.printMessage("ProtocolManager >>> Adding entry to reverse routing table: " + entry + "\n");
            routingTableManager.addReverseRoutingEntry(destAddr, destSeqNum, nextHop, hopCount, validDestSeqNum, lifetime);
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

        if (rreq.getLength() != 72) {
            console.printMessage("ProtocolManager >>> Invalid RREQ Length, not sending.\n");
            return;
        }

        console.printMessage("ProtocolManager >>> Sending RREQ: " + rreq.toString() + " with length " + rreq.getLength() + "bits.\n");
        // Encode RREQ
        String encodedRREQ = encodeBase64(rreq.toNumber().toByteArray());

        serialManager.writeData("AT+SEND=12");
        Thread.sleep(2000);
        serialManager.writeData(encodedRREQ);

        this.waitingForRREP = true;
        this.startTime = System.currentTimeMillis();
        waitForReply();
    }


    public void sendRREP(RREP routeReply) throws InterruptedException {
        BitString rrep = RREPtoBitString(routeReply);
        // Do not send if RREP is longer than 72 bits
        if (rrep.getLength() != 72) {
            console.printMessage("ProtocolManager >>> Invalid RREP Length, not sending.\n");
            return;
        }
        console.printMessage("ProtocolManager >>> Sending RREP: " + rrep.toString() + " with length " + rrep.getLength() + "bits.\n");
        String encodedRREP = encodeBase64(rrep.toNumber().toByteArray());
        serialManager.writeData("AT+SEND=12");
        Thread.sleep(2000);
        serialManager.writeData(encodedRREP);

    }

    public void sendDATA(DATA dataPacket) throws InterruptedException {
        // Check if route to destination exists
        System.out.println(convertAddressToString(dataPacket.getDestAddr().toString()));
        RoutingEntry route = null;
        if(convertAddressToString(dataPacket.getDestAddr().toString()).length() == 2) {
             route = findRoutingEntry("00" + convertAddressToString(dataPacket.getDestAddr().toString()));
        } else if (convertAddressToString(dataPacket.getDestAddr().toString()).length() == 1) {
             route = findRoutingEntry("000" + convertAddressToString(dataPacket.getDestAddr().toString()));
        }
        if (route != null) {
            BitString data = DATAtoBitString(dataPacket);
            console.printMessage("ProtocolManager >>> Sending DATA: " + data + " with length " + data.getLength() + "bits.\n");
            String encodedDATA = encodeBase64(data.toNumber().toByteArray());
            serialManager.writeData("AT+SEND=" + encodedDATA.getBytes().length);
            Thread.sleep(2000);
            serialManager.writeData(encodedDATA);
        } else {
            generateRREQ(convertAddressToString(dataPacket.getDestAddr().toString()));
            //generateRREQ(String.valueOf(dataPacket.getDestAddr()));
        }
    }

    public static String fromBinaryString(String binary) {
        byte[] data = new byte[binary.length() / 8];
        for (int i = 0; i < binary.length(); i++) {
            char c = binary.charAt(i);
            data[i / 8] = (byte) ((data[i / 8] << 1) | (c == '1' ? 1 : 0));
        }
        return new String(data, StandardCharsets.UTF_8);
    }

    // wait for reply in a loop


    private void waitForReply() throws InterruptedException {
        while (waitingForRREP) {
            if ((System.currentTimeMillis() - startTime) < currentWaitingTime) {
                Thread.sleep(50);
            } else {
                if (RREQRetries >= RREQ_RETRIES_MAX) {
                    console.printMessage("ProtocolManager >>> RREQ retries exceeded. No route found.\n");
                    waitingForRREP = false;
                    resetWaitingForRREP();
                    // TODO: Send Destination unreachable Message
                } else {
                    RREQRetries++;
                    this.localReq++;
                    this.startTime = System.currentTimeMillis();
                    currentWaitingTime = (int) Math.pow(2, RREQRetries) * NET_TRAVERSAL_TIME;
                    console.printMessage("ProtocolManager >>> RREQ Timeout. Retrying RREQ " + RREQRetries + " of " + RREQ_RETRIES_MAX + " times.\n");
                    sendRREQBroadcast(bufferedRREQ);
                }

            }
        }
        resetWaitingForRREP();
    }

    // Reset all relevant network parameters
    private void resetWaitingForRREP() {
        this.RREQRetries = 0;
        this.currentWaitingTime = NET_TRAVERSAL_TIME;
        this.startTime = 0;
    }


    /*
     *
     * Parsing and Processing Messages
     *
     */

    public void receiveIncomingPayload() throws InterruptedException {
        String payload;
        while (true) {
            if (serialManager.getCommandQueue().peek() != null) {
                payload = serialManager.getCommandQueue().poll();
                // Split LR,XXXX,XX, from Payload
                assert payload != null;
                String[] payloadSplit = payload.split(",");

                // Check if payload contains all necessary data
                if (payloadSplit.length < 4) {
                    console.printMessage("ProtocolManager >>> Received invalid payload: " + payload + "\n");
                    continue;
                }
                // Save [1] XXXX as prevHop
                this.prevHop = payloadSplit[1];
                // Save [3] the rest as payload
                String payloadString = payloadSplit[3];
                payloadString = payloadString.replace("\n", "").replace("\r", "");
                console.printMessage("ProtocolManager >>> Received payload: " + payloadString + "\n");

                if (payloadString.charAt(0) == 'A') {
                    console.printMessage("ProtocolManager >>> Received UD");
                    processDATA(payloadString);
                } else {

                    byte[] payloadBytes = decodeBase64(payloadString);
                    if (payloadBytes == null) {
                        continue;
                    }
                    String binaryData;
                    try {
                        binaryData = new BigInteger(1, payloadBytes).toString(2);
                    } catch (NullPointerException e) {
                        console.printMessage("ProtocolManager >>> Received invalid payload: " + payload + "\n");
                        continue;
                    }
                    parseMessageType(binaryData);
                }
            } else {
                Thread.sleep(2000);
            }
        }

    }


    public void parseMessageType(String incomingMessage) throws InterruptedException {
        if (incomingMessage.length() < 66) {
            console.printMessage("ProtocolManager >>> Received invalid message: " + incomingMessage + "\n");
            return;
        }
        String msgType = incomingMessage.substring(0, incomingMessage.length() - 66);
        int type;
        try {
            type = Integer.parseInt(msgType, 2);
        } catch (NumberFormatException e) {
            console.printMessage("ProtocolManager >>> Received invalid message: " + incomingMessage + "\n");
            return;
        }

        switch (type) {
            case 1 -> {
                console.printMessage("ProtocolManager >>> Payload parsed as RREQ.\n");
                processRREQ(incomingMessage);
            }
            case 2 -> {
                console.printMessage("ProtocolManager >>> Payload parsed as RREP.\n");
                waitingForRREP = false;
                processRREP(incomingMessage);
            }
            default -> console.printErrMessage("ProtocolManager >>> Received unknown message type: " + msgType);
        }
    }


    public void processRREQ(String incomingMessage) throws InterruptedException {

        //Parse Data from RREQ Frames
        //--------------------------------------------------------------------------------------------------------------
        String sourceAddr = incomingMessage.substring(incomingMessage.length() - 24, incomingMessage.length() - 8);
        String sourceSeqNum = incomingMessage.substring(incomingMessage.length() - 8);
        String destAddr = incomingMessage.substring(incomingMessage.length() - 48, incomingMessage.length() - 32);
        String reqID = incomingMessage.substring(incomingMessage.length() - 54, incomingMessage.length() - 48);
        String destSeqNum = incomingMessage.substring(incomingMessage.length() - 32, incomingMessage.length() - 24);
        String hopCount = incomingMessage.substring(incomingMessage.length() - 60, incomingMessage.length() - 54);
        //--------------------------------------------------------------------------------------------------------------
        //Choose between adding Route and Updating Route
        //--------------------------------------------------------------------------------------------------------------
        //Create or update route to the previous hop without a valid Sequence Number.
        //--------------------------------------------------------------------------------------------------------------
        //Compare RREQ_ID and Orig Address
        // Add to the list ?
        //--------------------------------------------------------------------------------------------------------------

        destAddr = convertAddressToString(destAddr);

        RoutingEntry entry = findRoutingEntry(destAddr);
        if (entry != null) {
            // If entry has no valid sequence number, update it
            if (!entry.isValidDestSeqNum() ||
                    Integer.parseInt(destSeqNum) > Integer.parseInt(entry.getDestSeqNum()) ||
                    (Integer.parseInt(destSeqNum) == Integer.parseInt(entry.getDestSeqNum()) && Integer.parseInt(hopCount) < Integer.parseInt(entry.getHopCount()))) {

                // Set Seq Number to max of current and received Seq Number
                String destSeqNumForEntry = String.valueOf(Math.max(Integer.parseInt(sourceSeqNum), Integer.parseInt(entry.getDestSeqNum())));
                // Update Lifetime to max of current and received Lifetime
                String lifeTime = String.valueOf(Math.max(Long.parseLong(entry.getLifetime()), System.currentTimeMillis() + 2 * NET_TRAVERSAL_TIME - 2L * Integer.parseInt(hopCount) * NET_TRAVERSAL_TIME));
                // Update Entry and set next hop to buffered prevHop
                entry.setDestSeqNum(destSeqNumForEntry);
                entry.setValidDestSeqNum(true);
                entry.setNextHop(this.prevHop);
                entry.setHopCount(hopCount);
                entry.setLifetime(lifeTime);
            }
            console.printMessage("ProtocolManager >>> Route to " + destAddr + " already exists.\n");

        } else {
            // Else set Seq Number to max of known sourceSeqNum and destSeqNum
            String destSeqNumForEntry = String.valueOf(Math.max(Integer.parseInt(sourceSeqNum), Integer.parseInt(destSeqNum)));
            // Set Valid Dest Seq Flag to false and calculate lifetime
            boolean validDestSeq = false;
            String lifeTime = String.valueOf(System.currentTimeMillis() + 2 * NET_TRAVERSAL_TIME - 2L * Integer.parseInt(hopCount) * NET_TRAVERSAL_TIME);
            addRoutingTable(destAddr, destSeqNumForEntry, this.prevHop, hopCount, validDestSeq, lifeTime, this.prevHop);
        }
        //--------------------------------------------------------------------------------------------------------------
        // Check reverse routing table if route exists
        //--------------------------------------------------------------------------------------------------------------
        ReverseRoutingEntry reverseRoutingEntry = findReverseRoutingEntry(sourceAddr);
        if (reverseRoutingEntry != null) {
            if (!reverseRoutingEntry.isValidDestSeqNum()
                    || Integer.parseInt(destSeqNum) > Integer.parseInt(reverseRoutingEntry.getDestSeqNum())
                    || (Integer.parseInt(destSeqNum) == Integer.parseInt(reverseRoutingEntry.getDestSeqNum())
                    && Integer.parseInt(hopCount) < Integer.parseInt(reverseRoutingEntry.getHopCount()))) {

                String destSeqNumForEntry = String.valueOf(Math.max(Integer.parseInt(destSeqNum), Integer.parseInt(reverseRoutingEntry.getDestSeqNum())));
                String lifeTime = String.valueOf(Math.max(Long.parseLong(reverseRoutingEntry.getLifetime()),
                        System.currentTimeMillis() + 2 * NET_TRAVERSAL_TIME - 2L * Integer.parseInt(hopCount) * NET_TRAVERSAL_TIME));

                reverseRoutingEntry.setDestSeqNum(destSeqNumForEntry);
                reverseRoutingEntry.setValidDestSeqNum(true);
                reverseRoutingEntry.setNextHop(this.prevHop);
                reverseRoutingEntry.setHopCount(hopCount);
                reverseRoutingEntry.setLifetime(lifeTime);

            }
            console.printMessage("ProtocolManager >>> Reverse Route from " + sourceAddr + " already exists.\n");
        } else {
            String lifeTime = String.valueOf(System.currentTimeMillis() + 2 * NET_TRAVERSAL_TIME - 2L * Integer.parseInt(hopCount) * NET_TRAVERSAL_TIME);
            addReverseRoutingTable(sourceAddr, sourceSeqNum, this.prevHop, hopCount, false, lifeTime, this.prevHop);
        }
        //--------------------------------------------------------------------------------------------------------------

        System.out.println(destAddr);
        System.out.println(ownAddr);

        if (destAddr.equals(ownAddr) || findRoutingEntry(destAddr).isValidDestSeqNum()) {
            console.printMessage("ProtocolManager >>> RREQ for own address received. Sending RREP.\n");
            // Generate new RREP
            RREP rrep = generateRREP(sourceAddr, destAddr, destSeqNum);
            // Send RREP
            sendRREP(rrep);

        } else {
            console.printMessage("ProtocolManager >>> RREQ for other address received. Forwarding...\n");
            // Increment HopCount
            BitString newHopCount = incrementFrame(hopCount, "%6s");
            // Broadcast updated RREQ
            RREQ newRREQ = new RREQ(
                    new BitString("000000"),
                    newHopCount,
                    new BitString(reqID),
                    new BitString(convertAddressToBinary(destAddr)),
                    new BitString(destSeqNum),
                    new BitString(sourceAddr),
                    new BitString(sourceSeqNum)
            );
            sendRREQBroadcast(newRREQ);
        }


    }

    public RREP generateRREP(String sourceAddr, String destAddr, String destSeqNum) {
        String lifeTime;

        if (destAddr.equals(ownAddr)) {
            console.printMessage("ProtocolManager >>> Generating RREP...\n");
            // Reset Hop Count to 0
            String hopCount = "00000000";
            // Set Lifetime to MY_ROUTE_TIMEOUT
            lifeTime = String.valueOf(MY_ROUTE_TIMEOUT);
            lifeTime = lifetimeToBinary(lifeTime);
            System.out.println(lifeTime + "|" + lifeTime.length());
            // Increment last known Seq Number
            if (localSeqNum + 1 == Integer.parseInt(destSeqNum)) {
                localSeqNum = localSeqNum + 1;
            }

            return new RREP(new BitString(lifeTime), new BitString(ownAddrAsBinary()), new BitString(localSeqAsBinary()), new BitString(sourceAddr), new BitString(hopCount));
        } else {
            RoutingEntry entry = findRoutingEntry(destAddr);
            ReverseRoutingEntry reverseEntry = findReverseRoutingEntry(sourceAddr);

            entry.addPrecursor(sourceAddr);
            reverseEntry.addPrecursor(entry.getNextHop());
            lifeTime = String.valueOf(System.currentTimeMillis() - Long.parseLong(entry.getLifetime()));
            lifeTime = lifetimeToBinary(lifeTime);
            System.out.println(lifeTime + "|" + lifeTime.length());

            return new RREP(new BitString(lifeTime), new BitString(ownAddrAsBinary()), new BitString(entry.getDestSeqNum()), new BitString(sourceAddr), new BitString(entry.getHopCount()));
        }
    }


    public void processRREP(String incomingMessage) {

        //Parse Data from RREP Frames
        //--------------------------------------------------------------------------------------------------------------
        String lifeTime = incomingMessage.substring(incomingMessage.length() - 66, incomingMessage.length() - 48);
        String destinationAddress = incomingMessage.substring(incomingMessage.length() - 48, incomingMessage.length() - 32);
        String destinationSequence = incomingMessage.substring(incomingMessage.length() - 32, incomingMessage.length() - 24);
        String originatorAddress = incomingMessage.substring(incomingMessage.length() - 24, incomingMessage.length() - 8);
        String hopCount = incomingMessage.substring(incomingMessage.length() - 8);
        //-----------------
        // ---------------------------------------------------------------------------------------------

        destinationAddress = convertAddressToString(destinationAddress);

        System.out.println(lifeTime + " " + destinationAddress + " " + destinationSequence + " " + hopCount + " " + originatorAddress );
        // Create new RREP for forwarding
        RREP rrep = null;
        try {
            rrep = new RREP(new BitString(lifeTime),
                    new BitString(convertAddressToBinary(destinationAddress)),
                    new BitString(destinationSequence),
                    new BitString(hopCount),
                    new BitString(originatorAddress));
        } catch (Exception e) {
            console.printErrMessage("ProtocolManager >>> Error while creating RREP for forwarding. " + e.getMessage() + "\n");
            return;
        }


        // Check Routing Table for existing routes
        RoutingEntry prevEntry = findRoutingEntry(prevHop);
        RoutingEntry entry = findRoutingEntry(destinationAddress);

        if (findRoutingEntry(prevHop) == null) {
            // TODO: Update Valid Flag
            addRoutingTable(prevHop, destinationSequence, prevHop, String.valueOf(1), false, String.valueOf(System.currentTimeMillis() + Long.parseLong(lifeTime)), null);

        } else if (!prevEntry.isValidDestSeqNum() ||
                Integer.parseInt(destinationSequence) > Integer.parseInt(prevEntry.getDestSeqNum()) ||
                (Integer.parseInt(destinationSequence) == Integer.parseInt(prevEntry.getDestSeqNum()) && Integer.parseInt(hopCount) < Integer.parseInt(prevEntry.getHopCount()))) {
            prevEntry.setActive(true);
            prevEntry.setValidDestSeqNum(true);
            prevEntry.setNextHop(prevHop);
            prevEntry.setHopCount(String.valueOf(1));

        }

        if (entry == null || prevEntry == null) {
            addRoutingTable(prevHop, destinationSequence, prevHop, hopCount, false, lifeTime, originatorAddress);
        } else if (!prevEntry.isValidDestSeqNum() ||
                Integer.parseInt(destinationSequence) > Integer.parseInt(prevEntry.getDestSeqNum())
                || (Integer.parseInt(destinationSequence) == Integer.parseInt(prevEntry.getDestSeqNum())
                && Integer.parseInt(hopCount) < Integer.parseInt(prevEntry.getHopCount()))) {
            String destSeqNumForEntry = String.valueOf(Math.max(Integer.parseInt(destinationSequence), Integer.parseInt(entry.getDestSeqNum())));

            entry.setActive(true);
            entry.setValidDestSeqNum(true);
            entry.setNextHop(prevHop);
            entry.setHopCount(hopCount);
            entry.setLifetime(String.valueOf(Long.parseLong(entry.getLifetime()) + Long.parseLong(lifeTime)));
            entry.setDestSeqNum(destSeqNumForEntry);
        }

        if (originatorAddress.equals(ownAddr)) {
            console.printMessage("ProtocolManager >>> RREP for own address received.\n");
            String life = String.valueOf(Long.parseLong(lifeTime) + System.currentTimeMillis());
            routingTableManager.addRoutingEntry(new RoutingEntry(destinationAddress, destinationSequence, prevHop, hopCount, true, life));
        } else {
            console.printMessage("ProtocolManager >>> RREP for other address received. Forwarding...\n");
            // Increment HopCount
            rrep.setHopCount(incrementFrame(hopCount, "%6s"));
            // Update Destination Address
            if (findReverseRoutingEntry(destinationAddress) != null) {
                rrep.setDestAddr(new BitString(findReverseRoutingEntry(destinationAddress).getDestAddr()));
            } else {
                rrep.setDestAddr(new BitString(destinationAddress));
            }
            // Try to send RREP
            try {
                sendRREP(rrep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // If the buffered Entry is not null, add the given previous hop to the precursor list of the entry
        if (entry != null) {
            entry.addPrecursor(prevHop);
        }

        ReverseRoutingEntry revEntry = findReverseRoutingEntry(destinationAddress);
        if (revEntry != null) {
            revEntry.setLifetime(String.valueOf(Math.max(Long.parseLong(revEntry.getLifetime()), System.currentTimeMillis() + ACTIVE_ROUTE_TIMEOUT)));
        }
        if (prevEntry != null) {
            prevEntry.addPrecursor(originatorAddress);
        }
    }


    private void processDATA(String incomingMessage) {

        byte[] decodedPayloadBytes = decodeBase64(incomingMessage);
        decodedPayloadBytes = userDataPacketDecode(decodedPayloadBytes);
        byte[] dest = getRangeArray(decodedPayloadBytes, 1, 4);
        // put all bytes in one string
        StringBuilder sb = new StringBuilder();
        for (byte b : dest) {
            sb.append(b);
        }
        String destString = sb.toString();
        //TODO: if destAddr not node addr -> RREQ for destAddr
        String message = new String(getRangeArray(decodedPayloadBytes, 5, decodedPayloadBytes.length - 1));
        console.printMessage("ProtocolManager >>> Received Message: " + message + " from Address: " + destString);

    }

    // Create RREQ Message with a specific destination address
    public void generateRREQ(String destAddr) throws InterruptedException {
        BitString destSeqNum = new BitString("0");
        BitString flag = new BitString("000000");
        // Check if Route already exists
        RoutingEntry routingEntry = findRoutingEntry(destAddr);
        if (routingEntry != null && routingEntry.isValidDestSeqNum()) {
            console.printMessage("ProtocolManager >>> Route to " + destAddr + " already exists.\n");
            return;
        } else if (routingEntry == null) {
            destSeqNum = new BitString("00000000");
            flag = new BitString("100000");

        } else if (!routingEntry.isValidDestSeqNum()) {
            destSeqNum = new BitString(routingEntry.getDestSeqNum());
        }

        // Increment Req ID and Seq Number
        localSeqNum = localSeqNum + 1;
        localReq = localReq + 1;
        // Convert to BitStrings and generate RREQ
        String localSeqAsBinary = localSeqAsBinary();
        String localReqAsBinary = localReqAsBinary();
        String destAddrBinary = String.format("%16s", Integer.toBinaryString(Integer.parseInt(destAddr, 16))).replace(' ', '0');
        String ownAddrBinary = ownAddrAsBinary();
        RREQ req = new RREQ(
                // Flags
                flag,
                // Hop Count set to 0
                new BitString("000000"),
                // Req ID set to local RequestID
                new BitString(localReqAsBinary),
                // Dest Addr
                new BitString(destAddrBinary),
                // Updated Dest Seq
                destSeqNum,
                // Origin Addr set to own address
                new BitString(ownAddrBinary),
                // Origin Seq set to own sequence number
                new BitString(localSeqAsBinary)
        );
        sendRREQBroadcast(req);
    }

    public void generateDATA(String message, String destAddr) {
        destAddr = convertAddressToBinary(destAddr);

        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        // convert data to binary string
        StringBuilder dataAsBinary = new StringBuilder();
        for (byte b : data) {
            dataAsBinary.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }


        DATA dataPacket = new DATA(
                new BitString(dataAsBinary.toString()),
                new BitString(destAddr)
        );
        try {
            sendDATA(dataPacket);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    //--------------------------------------------------------------------------------------------------------------
    // Utility Methods (Decoding, Encoding, Incrementing, Parsing etc.)
    //--------------------------------------------------------------------------------------------------------------
    public static byte[] userDataPacketEncode(byte[] udPacket) {

        var a = Arrays.copyOfRange(udPacket, 0, 4);
        var fB = (byte) ((a[1] & 0x0c) >> 2);
        var sB = (byte) (((a[1] & 0x03) << 6) | ((a[2] & 0x0f) << 2) | ((a[3] & 0x0c) >> 2));
        var tB = (byte) (((a[3] & 0x03) << 6) | ((a[4] & 0x0f) << 2));
        var b = Arrays.copyOfRange(udPacket, 5, udPacket.length - 1);
        var res = new byte[b.length];
        for (int i = 0; i < res.length; i++) {
            if (i == 0) {
                tB = (byte) (tB | ((b[i] & 0xc0) >> 6));
            } else if (i == res.length - 1) {
                res[i] = (byte) (b[i] << 2);
                continue;
            }
            res[i] = (byte) ((b[i] << 2) | ((b[i + 1] & 0xc0) >> 6));

        }
        return ArrayUtils.addAll(new byte[]{fB, sB, tB}, res);
    }
    private String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);

    }

    private byte[] decodeBase64(String base64String) {
        try {
            return Base64.getDecoder().decode(base64String);
        } catch (IllegalArgumentException e) {
            console.printErrMessage("Received Illegal Payload");
            return null;
        }
    }

    private byte[] userDataPacketDecode(byte[] udPacket) {

        byte[] firstThreeBytes = Arrays.copyOfRange(udPacket, 0, 3);

        byte type = (byte) (firstThreeBytes[0] >> 2);
        byte addr0 = (byte) (((firstThreeBytes[0] & 0x03) << 2) | ((firstThreeBytes[1] & 0xc0) >> 6));
        byte addr1 = (byte) ((firstThreeBytes[1] & 0x3C) >> 2);
        byte addr2 = (byte) (((firstThreeBytes[1] & 0x03) << 2) | ((firstThreeBytes[2] & 0xc0) >> 6));
        byte addr3 = (byte) ((firstThreeBytes[2] & 0x3C) >> 2);

        byte[] remainingBytes = Arrays.copyOfRange(udPacket, 3, udPacket.length);

        // Add the last 2 bits of the third byte to the beginning of the remaining bytes
        byte[] modifiedRemainingBytes = new byte[remainingBytes.length + 1];
        modifiedRemainingBytes[0] = (byte) (firstThreeBytes[2] & 0x03);
        System.arraycopy(remainingBytes, 0, modifiedRemainingBytes, 1, remainingBytes.length);

        // Decode the remaining bytes
        byte[] decodedBytes = new byte[modifiedRemainingBytes.length - 1];
        for (int i = 0; i < decodedBytes.length; i++) {
            decodedBytes[i] = (byte) (((modifiedRemainingBytes[i] & 0x03) << 6) | ((modifiedRemainingBytes[i + 1] & 0xfc) >> 2));
        }

        return ArrayUtils.addAll(new byte[]{type, addr0, addr1, addr2, addr3}, decodedBytes);
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

    // Convert a RREP to BitString
    public BitString RREPtoBitString(RREP routeReply) {
        return new BitString(
                routeReply.getType().toString() +
                        routeReply.getLifetime() +
                        routeReply.getDestAddr() +
                        routeReply.getDestSeq() +
                        routeReply.getSourceAddr() +
                        routeReply.getHopCount()


        );
    }

    // Convert a DATA Packet to BitString
    private BitString DATAtoBitString(DATA dataPacket) {
        return new BitString(dataPacket.getType().toString() + dataPacket.getMessage());
    }

    // Convert Local Request ID to binary String
    private String localReqAsBinary() {
        return String.format("%6s", Integer.toBinaryString(localReq)).replace(' ', '0');
    }

    // Convert Local Sequence Number to binary String
    private String localSeqAsBinary() {
        return String.format("%8s", Integer.toBinaryString(localSeqNum)).replace(' ', '0');
    }

    // Convert own Address to binary String
    private String ownAddrAsBinary() {
        return String.format("%16s", Integer.toBinaryString(Integer.parseInt(ownAddr, 16))).replace(' ', '0');
    }

    private String lifetimeToBinary(String lifetime) {
        return String.format("%18s", Integer.toBinaryString(Integer.parseInt(lifetime))).replace(' ', '0');
    }

    private String binaryLifetimeToString(String binaryLifetime) {
        return String.valueOf(Long.parseLong(binaryLifetime, 2));
    }

    // Increment a specific Message Packet Frame and convert it back to BitString
    public BitString incrementFrame(String frame, String format) {
        BitString frameBitString = new BitString(frame);
        int frameCount = Integer.parseInt(frameBitString.toString(), 2);
        frameCount++;
        return new BitString(String.format(format, Integer.toBinaryString(frameCount)).replace(' ', '0'));
    }

    public String convertAddressToString(String address) {
        return Integer.toString(Integer.parseInt(address, 2), 16);
    }

    public String convertAddressToBinary(String address) {
        return String.format("%16s", Integer.toBinaryString(Integer.parseInt(address, 16))).replace(' ', '0');
    }

    // Access to the Routing Tables for the LoraCLI
    public RoutingTableManager getRoutingTableManager() {
        return routingTableManager;
    }

    private byte[] getRangeArray(byte[] arr, int from, int to) {
        var res = new byte[to - from + 1];
        if (to - from + 1 >= 0) System.arraycopy(arr, from, res, 0, to - from + 1);
        return res;
    }


    //--------------------------------------------------------------------------------------------------------------
    // Legacy Methods (Not used anymore)
    //--------------------------------------------------------------------------------------------------------------

    /*
    public void generateRREQ(String destAddr) throws InterruptedException {

        // Check if Route already exists
        RoutingEntry routingEntry = findRoutingEntry(destAddr);
        if (routingEntry != null && routingEntry.isValidDestSeqNum()) {
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
    }*/

      /*
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
    }*/


    /*
     *
     * Message Decoding and Encoding
     *
     */

       /*
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
            addRoutingTable(destAddr, destSeqNum, null, hopCount, this.prevHop);
        }

        // Check reverse routing table if route exists
        ReverseRoutingEntry reverseRoutingEntry = findReverseRoutingEntry(sourceAddr);
        if (reverseRoutingEntry != null) {
            console.printMessage("ProtocolManager >>> Reverse Route from " + sourceAddr + " already exists.\n");
            int pos = routingTableManager.getReverseRoutingTable().indexOf(reverseRoutingEntry);
            // routingTableManager.getReverseRoutingTable().get(pos).setSeq;
        } else {
            addReverseRoutingTable(sourceAddr, sourceSeqNum, reqID, this.prevHop);
        }


        if (destAddr.equals(ownAddr)) {
            console.printMessage("ProtocolManager >>> RREQ for own address received. Sending RREP.\n");
            // Hop Count to 0
            BitString hop = new BitString("00000000");


            BitString lifetime = new BitString("0000000000000000");

            BitString destSeq = incrementFrame(destSeqNum, "%8s");
            String localSeqAsBinary = String.format("%8s", Integer.toBinaryString(localSeqNum)).replace(' ', '0');
            BitString localSeq = new BitString(localSeqAsBinary);
            if (!destSeqNum.equals(localSeqAsBinary)) {
                destSeq = localSeq;
            }
            // Generate new RREP
            RREP rrep = new RREP(lifetime, new BitString(sourceAddr), destSeq, new BitString(ownAddrAsBinary()), hop);
            // Send RREP
            sendRREP(rrep);

        } else {
            console.printMessage("ProtocolManager >>> RREQ for other address received. Forwarding...\n");
            ;
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
    }*/

    /*
     private void waitForReply2() throws InterruptedException {
        if (waitingForRREP && RREQRetries < RREQ_RETRIES_MAX) {

            while ((System.currentTimeMillis() - startTime) < currentWaitingTime) {
                console.printMessage("ProtocolManager >>> Waiting for RREP...");
            }
                console.printMessage("ProtocolManager >>> RREP not received, retrying...");
                // Back off timer for retries
                currentWaitingTime = (int) Math.pow(2, RREQRetries) * NET_TRAVERSAL_TIME;
                // Restart timer and increase retries, reqID and buffer RREQ
                this.startTime = System.currentTimeMillis();
                RREQRetries++;
                this.localReq++;
                this.bufferedRREQ.setReq(new BitString(String.format("%6s", Integer.toBinaryString(localReq)).replace(' ', '0')));
                sendRREQBroadcast(bufferedRREQ);
                waitForReply();

        } else {
            console.printMessage("ProtocolManager >>> RREP not received, giving up...");
            // Reset all changed net parameters
            currentWaitingTime = NET_TRAVERSAL_TIME;
            RREQRetries = 0;
            waitingForRREP = false;
            bufferedRREQ = null;
        }
    }
     */

}
