package lev.philippov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;


public class ClientHandler {
    private Server server;
    private Socket socket;
    private Logger logger;
    protected static final int AUTH = 1;
    protected static final int MSG = 2;
    protected static final int SRVS = 3;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;


    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
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
    @Deprecated
    private void listeningWithDataOS() throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        Thread t = new Thread(() -> {
            try {
               while (true) {
//                       int x;
//                       while ((x=dis.read())!=-1) {
//                           System.out.println(x);
//                       }
                   String message= dis.readUTF();
                    if (message.equals("/end")) break;
                   System.out.println(message);
                   dos.writeUTF("ECHO: "+ message);
               }
            } catch (IOException e) {
                logger.error(e.getMessage());
            } finally {
                closeConnection(dis);
            }
        });
        t.setDaemon(true);
        t.start();
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
                if(server.checkReg(authMsg.getLogin(), authMsg.getPassword()))  {
                    msgSender(AUTH, authMsg);
                }
            }
            if (obj instanceof ChatMsg) {
                server.broadcast((ChatMsg)obj);
            }
    }

    public void msgSender(int type, Serializable obj) {
        try {
            switch (type) {
                case AUTH:
                    AuthMsg authMsg = ((AuthMsg) obj);
                    authMsg.setPassword("");
                    oos.writeObject(authMsg);
                    logger.info("Клиенту с логином " + authMsg.getLogin() + " отправлено подтверждение об успешной регистрации.");
                    break;
                case MSG:
                    oos.writeObject(obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
