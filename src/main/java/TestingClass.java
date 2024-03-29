/**
 * Class to try things out
 */

import control.LoraCLI;
import model.messageTypes.DATA;
import model.protocol.ProtocolManager;
import org.uncommons.maths.binary.BitString;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class TestingClass {

    public static void main(String[] args) throws InterruptedException {

        LoraCLI loraCLI = new LoraCLI();
        // loraCLI.start();

   /*     BitString bitString = new BitString("000001");
        // parse to int
        int type__ = Integer.parseInt(bitString.toString(), 2);
        System.out.println(type__);
        type__ = type__ + 1;
        // Convert to binary
        String binary = String.format("%6s", Integer.toBinaryString(type__)).replace(' ', '0');
        bitString = new BitString(binary);
        System.out.println(bitString.toString());


        String destAddr = "0007";
        String sourceAddr = "0008";

        int destSeq_ = 1;
        int sourceSeq_ = 25;
        // Convert destAddr to binary string
        String destAddrBinary = String.format("%16s", Integer.toBinaryString(Integer.parseInt(destAddr, 16))).replace(' ', '0');
        // Convert sourceAddr to binary string
        String sourceAddrBinary = String.format("%16s", Integer.toBinaryString(Integer.parseInt(sourceAddr, 16))).replace(' ', '0');


        String d_seq = String.format("%8s", Integer.toBinaryString(destSeq_)).replace(' ', '0');
        String s_seq = String.format("%8s", Integer.toBinaryString(sourceSeq_)).replace(' ', '0');
        System.out.println("\n*******************************************");
        System.out.println("Dest Seq Binary: " + d_seq);
        System.out.println("Source Seq Binary: " + s_seq);

        // RREQ CONSTRUCTION
        BitString type = new BitString("000001");
        BitString flags = new BitString("000000");
        BitString hopCount = new BitString("000001");
        BitString reqID = new BitString("000000");
        BitString destAddrS = new BitString(destAddrBinary);
        BitString destSeq = new BitString(d_seq);
        BitString sourceAddrS = new BitString(sourceAddrBinary);
        BitString sourceSeq = new BitString(s_seq);

        // ENCODING AND DECODING
        BitString payload = new BitString(type.toString() + flags + hopCount + reqID + destAddrS + destSeq + sourceAddrS + sourceSeq);
        // get total length of payload
        int payloadLength = payload.getLength();
        // print payload
        System.out.println("Payload Binary: " + payload);
        System.out.println("Payload Length: " + payloadLength);
        // encode payload to base64
        String encodedPayload = Base64.getEncoder().encodeToString(payload.toNumber().toByteArray());
        // print encoded payload
        System.out.println("Encoded Base64 String: " + encodedPayload);

        // INTERPRETATION OF RECEIVED MESSAGE
        // decode payload from base64
        byte[] decodedPayload = Base64.getDecoder().decode(encodedPayload);
        // print decoded payload
        System.out.println("Decoded Payload Bytes: " + Arrays.toString(decodedPayload));
        String binaryStr = new BigInteger(1, decodedPayload).toString(2);
        System.out.println("Decoded Payload Binary: " + binaryStr);
        System.out.println("Decoded Payload Length: " + binaryStr.length());
        System.out.println("*******************************************");
        // get last 8 bits
        String srcSeq = binaryStr.substring(binaryStr.length() - 8);
        System.out.println("Source Seq " + srcSeq);
        // get next 16 bits
        String srcAddr = binaryStr.substring(binaryStr.length() - 24, binaryStr.length() - 8);
        System.out.println("Source Addr " + srcAddr);
        // get next 8 bits
        String destiSeq = binaryStr.substring(binaryStr.length() - 32, binaryStr.length() - 24);
        System.out.println("Dest Seq " + destiSeq);
        // get next 16 bits
        String destiAddr = binaryStr.substring(binaryStr.length() - 48, binaryStr.length() - 32);
        System.out.println("Dest Addr " + destiAddr);
        // get next 6 bits
        String reqID_ = binaryStr.substring(binaryStr.length() - 54, binaryStr.length() - 48);
        System.out.println("Req ID " + reqID_);
        // get next 6 bits
        String hopCount_ = binaryStr.substring(binaryStr.length() - 60, binaryStr.length() - 54);
        System.out.println("Hop Count " + hopCount_);
        // get next 6 bits
        String flags_ = binaryStr.substring(binaryStr.length() - 66, binaryStr.length() - 60);
        System.out.println("Flags " + flags_);
        // get remaining bits
        String type_ = binaryStr.substring(0, binaryStr.length() - 66);
        System.out.println("Type " + Integer.parseInt(type_, 2));

        System.out.println("*******************************************");


        String lora = "LR,XXXX,XX,";
        String pl = lora + encodedPayload;
        System.out.println("Encoded Payload with Lora Addition --> " + pl);
        String pload = pl.split(",")[3];
        System.out.println("Payload --> " + pload);
        byte[] decodedPayload_ = Base64.getDecoder().decode(pload);
        System.out.println("Decoded Payload Bytes: " + Arrays.toString(decodedPayload_));

        ProtocolManager protocolManager = new ProtocolManager(null);
        // protocolManager.parseMessageType(test);
        // protocolManager.receiveIncomingPayload(pl.getBytes());


        long time = 6000;
        String timeBinary = String.format("%18s", Integer.toBinaryString((int) time)).replace(' ', '0');
        System.out.println("Time Binary: " + timeBinary);
        // convert to long
        long time_ = Long.parseLong(timeBinary, 2);
        System.out.println("Time: " + time_);


        BitString data = new BitString("000000100101011010101100000101010010101010101010101010101001010101010110100");
        String typesss = data.toString().substring(0, 6);
        System.out.println("Type: " + typesss);
        String destAddrss = data.toString().substring(6, 22);
        System.out.println("Dest Addr: " + destAddrss);
        String data_;
        if(data.getLength() > 48) {
            data_ = data.toString().substring(22, 48);
        } else {
            data_ = data.toString().substring(22, data.getLength());
        }
        System.out.println(data_);
        System.out.println(data_.length());*/
        BitString type = new BitString("000000");
        int dest = 5;
        String destBinary = String.format("%16s", Integer.toBinaryString(dest)).replace(' ', '0');
        BitString destAddr = new BitString(destBinary);

        byte[] typeBinary = type.toNumber().toByteArray();
        byte[] destAddressBinary = destAddr.toNumber().toByteArray();


        String message = "Hallo";

        byte[] binaryMessage = new byte[typeBinary.length + destAddressBinary.length + message.length()];

        System.arraycopy(typeBinary, 0, binaryMessage, 0, typeBinary.length);
        System.arraycopy(destAddressBinary, 0, binaryMessage, typeBinary.length, destAddressBinary.length);

        for (int i = 0; i < message.length(); i++) {
            int c = message.charAt(i);
            binaryMessage[typeBinary.length + destAddressBinary.length + i] = (byte) c;
        }

        String encodedMessage = Base64.getEncoder().encodeToString(binaryMessage);
        System.out.println(encodedMessage);
        System.out.println("AAAVIYWxsbw=");
    }


}

