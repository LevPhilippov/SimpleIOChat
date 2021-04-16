package lev.philippov;

import lev.philippov.Models.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;


public class ClientHandler {

    private Client client;
    private Server server;
    private Socket socket;
    private Logger logger;
    private boolean authFlag;
    protected static final int AUTH = 1;
    protected static final int MSG = 2;
    protected static final int SRVS = 3;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;


    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        this.authFlag=false;
        this.logger = LoggerFactory.getLogger(this.getClass().getName());
        handleTheClient();
    }

    public void handleTheClient() {
        try {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());

            Thread listeningThread = new Thread(()->{
                try{
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
            if (obj instanceof AuthMsg) {
                AuthMsg authMsg = (AuthMsg) obj;
                if(server.checkReg(authMsg, this)) {
                    msgSender(AUTH, new SrvsMsg(), null);
                }
            }
            if (obj instanceof ChatMsg && authFlag) {
                server.broadcast((ChatMsg)obj);
            }
    }

    public void msgSender(int type, Serializable obj, String message) {
        try {
            switch (type) {
                //TODO: переделать на класс SrvsMsg
                case AUTH: {
                    SrvsMsg srvsMsg = (SrvsMsg) obj;
                    srvsMsg.setType(AUTH);
                    srvsMsg.setField_1(client.getName());
                    oos.writeObject(srvsMsg);
                    logger.info("Клиенту с именем " + client.getName() + " отправлено подтверждение об успешной регистрации.");
                    break;
                }
                case MSG: {
                    oos.writeObject(obj);
                    break;
                }
                case SRVS: {
                    SrvsMsg srvsMsg = (SrvsMsg) obj;
                    srvsMsg.setType(SRVS);
                    srvsMsg.setField_1(message);
                    oos.writeObject(srvsMsg);
                    break;
                }
            }
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

    public void setAuthenticated(){
        authFlag=true;
    }

    public void disconnect() {
        msgSender(ClientHandler.SRVS, new SrvsMsg(), "Вы были отключены от сервера.");
        closeObjectStreamConnections();
    }
}
