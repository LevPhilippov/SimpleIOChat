package lev.philippov;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

@Getter
@Setter
public class Network {

    private Controller controller;
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Logger networkLogger;
    private String name;


    public Network(Controller controller) {
        this.controller = controller;
        networkLogger = LoggerFactory.getLogger(this.getClass().getName());
        networkLogger.info("Network logger is ready!");
        initiateConnection();
    }


    public void initiateConnection() {
        try {
            this.socket = new Socket("localhost", 8189);
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());

            Thread listeningThread = new Thread(()->{
                try{
                    while(true){
                       Object obj =  ois.readObject();
                        msgResolver(obj);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    networkLogger.error(e.getMessage());
                } finally {
                    closeObjectStreamConnections();
                }

            });
            listeningThread.setDaemon(true);
            listeningThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        controller.logOut();
    }



    public void sendObj(String msg) {
        ChatMsg chatMsg = ChatMsg.builder().message(msg).name(name).build();
        try {
            oos.writeObject(chatMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendAuthMsg(String login, String password) {
        AuthMsg auth = AuthMsg.builder().login(login).password(DigestUtils.md5Hex(password).toUpperCase()).build();
        try {
            oos.writeObject(auth);
        } catch (IOException e) {
            networkLogger.error(e.getMessage());
        }
    }

    public void msgResolver(Object obj) {
        if (obj instanceof SrvsMsg) {
            SrvsMsg srvsMsg = (SrvsMsg) obj;
            if(srvsMsg.getType() == 1) {
                networkLogger.info("Сервер прислал сообщение об успешной регистрации.");
                name = ((SrvsMsg) obj).getField_1();
                controller.logIn();
            }
            if(srvsMsg.getType()==3) {
                networkLogger.info("Сервер прислал сообщение: " + srvsMsg.getField_1());
                controller.receiveMsg("Server: " + srvsMsg.getField_1());
            }
        }
        if (obj instanceof ChatMsg) {
                ChatMsg msg = ((ChatMsg) obj);
//                networkLogger.info("Сервер прислал новое собщение");
                StringBuilder builder = new StringBuilder();
                builder.append(msg.getName()).append(": ").append(msg.getMessage());
                controller.receiveMsg(builder.toString());
        }
    }

}
