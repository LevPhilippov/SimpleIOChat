package lev.philippov;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
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
    private String login;


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
    }

    private void startTexting() throws IOException {
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    String msg = in.readUTF();
                    System.out.println(Method.class.getName() + ": " + msg);
                    controller.receiveMsg(msg);
                }
            } catch (IOException ignored) {
            } finally {
                closeConnection();
            }
        });
        t.setDaemon(true);
        t.start();
    }

//    private void authorization() {
//            Thread t = new Thread(() -> {
//            SrvsMsg ms = new SrvsMsg();
//            msgToken.setToken(Token.AUTH);
//            try {
//                oos.writeObject(msgToken);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            while (true) {
//                try {
//                    if(Thread.currentThread().isInterrupted()) break;
//                    Object obj = ois.readObject();
//                    if(obj instanceof SrvsMsg) {
//                        SrvsMsg ansToken = (SrvsMsg) obj;
//                    } else {
//                        Thread.currentThread().interrupt();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//            networkLogger.info("Поток авторизации закрылся с флагом isInterrupted";
//        });
//    }

    public void sendObj(String msg) {
        ChatMsg chatMsg = ChatMsg.builder().message(msg).name(login).build();
        try {
            oos.writeObject(chatMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void sendMsg(String msg) {
//        try {
//            out.writeUTF(msg);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void sendAuthMsg(String login, String password) {
        AuthMsg auth = AuthMsg.builder().login(login).password(password).build();
        try {
            oos.writeObject(auth);
        } catch (IOException e) {
            networkLogger.error(e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            socket.close();
            networkLogger.info("Socket closed.");
        } catch (IOException e) {
            networkLogger.error(Method.class.getName() + ": ошибка при закрытии socket.\n" + e.getMessage());
        }
        try {
            in.close();
            networkLogger.info("In closed.");
        } catch (IOException e) {
            networkLogger.error(Method.class.getName() + ": ошибка при закрытии InputStream.\n" + e.getMessage());

        }
        try {
            out.close();
            networkLogger.info("Out closed.");
        } catch (IOException e) {
            networkLogger.error(Method.class.getName() + ": ошибка при закрытии Output stream.\n" + e.getMessage());
        }

    }

    public void msgResolver(Object obj) {
        if (obj instanceof AuthMsg) {
                networkLogger.info("Сервер прислал сообщение об успешной регистрации.");
                login = ((AuthMsg) obj).getLogin();
                controller.logIn();
        }
        if (obj instanceof ChatMsg) {
                ChatMsg msg = ((ChatMsg) obj);
                networkLogger.info("Сервер прислал новое собщение");
                StringBuilder builder = new StringBuilder();
                builder.append(msg.getName()).append(": ").append(msg.getMessage());
                controller.receiveMsg(builder.toString());
        }
    }

}
