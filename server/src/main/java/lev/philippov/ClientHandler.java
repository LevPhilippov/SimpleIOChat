package lev.philippov;

import lev.philippov.Models.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Map;


public class ClientHandler {

    private Client client;
    private Server server;
    private Socket socket;
    private Logger logger;
    private boolean authFlag;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;


    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        this.authFlag = false;
        this.logger = LoggerFactory.getLogger(this.getClass().getName());
        handleTheClient();
    }

    public void handleTheClient() {
        try {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());

            Thread listeningThread = new Thread(() -> {
                try {
                    while (true) {
                        Object obj = ois.readObject();
                        msgResolver(obj);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    logger.error(e.getMessage());
                } finally {
                    closeObjectStreamConnections();
                }

            });
            listeningThread.setDaemon(true);
            listeningThread.start();


//            listeningWithDataOS();

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void closeConnection(DataInputStream dis) {
        try {
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Потоки клиента закрылись");
    }

    private void closeObjectStreamConnections() {
        server.unsubscribeClientToServer(this);
        try {
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void msgResolver(Object obj) {
        if (obj instanceof SrvsMsg) {
            SrvsMsg msg = (SrvsMsg) obj;
            Map<String, String> params = msg.getParams();
            //Новая фича

            if (params.containsKey("AUTH")) {
                msg = server.checkReg(msg, this);
                msgSender(msg);
            } else {
                for (Map.Entry<String, String> e : params.entrySet()) {
                    switch (e.getKey()) {
                        case ("nickName"):
                            server.changeNickname(client, e.getValue(), this);
                            break;
                    }
                }
            }
        }

        if (obj instanceof ChatMsg && authFlag) {
            server.broadcast((ChatMsg) obj);
        }
    }

    public void msgSender(Serializable obj) {
        try {
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setAuthenticated() {
        authFlag = true;
    }

    public void disconnect() {
        closeObjectStreamConnections();
    }
}
