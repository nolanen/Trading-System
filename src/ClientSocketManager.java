import com.ib.client.EClientSocket;
import com.ib.client.EReader;

public class ClientSocketManager {
    final protected EClientSocket clientSocket;
    final private EReaderSignalImpl eReaderSignal;

    public ClientSocketManager(EWrapperImpl eWrapper) {
        eReaderSignal = new EReaderSignalImpl();
        clientSocket = new EClientSocket(eWrapper, eReaderSignal);
        connect();
    }

    public void connect() {
        String host = EnvVariables.get("HOST");
        int port = Integer.parseInt(EnvVariables.get("PORT"));
        int clientId = Integer.parseInt(EnvVariables.get("CLIENT_ID"));
        clientSocket.eConnect(host, port, clientId);

        EReader reader = new EReader(clientSocket, eReaderSignal);

        reader.start();

        new Thread(() -> {
            while (clientSocket.isConnected()) {
                eReaderSignal.waitForSignal();
                try {
                    reader.processMsgs();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }).start();
    }

    public void disconnect() {
        if(clientSocket.isConnected()) {
            clientSocket.eDisconnect();
        }
    }

    public boolean isConnected() {
        return clientSocket.isConnected();
    }
}

