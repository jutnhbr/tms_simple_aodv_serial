package threads;

import model.protocol.ProtocolManager;

public class ProtocolThread extends Thread {

    private final ProtocolManager protocolManager;

    public ProtocolThread(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }


    public void run() {
        try {
            protocolManager.receiveIncomingPayload();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

