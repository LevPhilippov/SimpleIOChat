package lev.philippov;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Map;

@Getter
@Setter
public class Network {

    private Controller controller;
    private Socket socket = null;
//    private DataInputStream in = null;
//    private DataOutputStream out = null;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Logger networkLogger;
    private String nickName;


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



//    public void sendObj(String msg) {
//        ChatMsg chatMsg = ChatMsg.builder().message(msg).nickName(nickName).build();
//        sendObj(msg);
//    }
    public void sendObj(Serializable obj) {
        if (obj instanceof String) {
            obj = ChatMsg.builder().message((String) obj).nickName(nickName).build();
        }
        try {
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSrvsMsg(String... strings) {
        SrvsMsg msg = new SrvsMsg();
        for (int i = 0; i <= strings.length-1;i=i+2) {
            msg.getParams().put(strings[i],strings[i+1]);
        }
        sendObj(msg);
    }

    public void msgResolver(Object obj) {
        if (obj instanceof SrvsMsg) {
            SrvsMsg srvsMsg = (SrvsMsg) obj;
            if (srvsMsg.getParams().containsKey("AUTH")) {
                if (srvsMsg.getParams().get("AUTH").equals("true")) {
                    networkLogger.info("Сервер прислал сообщение об успешной регистрации.");
                    nickName = srvsMsg.getParams().get("nickName");
                    controller.logIn();
                } if (!srvsMsg.getParams().get("AUTH").equals("true")) {
                    // отображение неуспешной регистрации
                }
            } else {
                for (Map.Entry<String, String> e : srvsMsg.getParams().entrySet()) {
                    switch (e.getKey()){
                        case "nickName":
                            nickName = e.getValue();
                            break;
                    }

                }
            }
        }

        if (obj instanceof ChatMsg) {
                ChatMsg msg = ((ChatMsg) obj);
                StringBuilder builder = new StringBuilder();
                builder.append(msg.getNickName()).append(": ").append(msg.getMessage());
                controller.receiveMsg(builder.toString());
        }
    }

}
