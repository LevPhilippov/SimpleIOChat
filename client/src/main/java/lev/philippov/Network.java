package lev.philippov;

import jdk.internal.util.xml.impl.Input;
import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
    private File history;


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

            Thread listeningThread = new Thread(() -> {
                try {
                    while (true) {
                        Object obj = ois.readObject();
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
        for (int i = 0; i <= strings.length - 1; i = i + 2) {
            msg.getParams().put(strings[i], strings[i + 1]);
        }
        sendObj(msg);
    }

    public void msgResolver(Object obj) {
        if (obj instanceof SrvsMsg) {
            SrvsMsg srvsMsg = (SrvsMsg) obj;
            if (srvsMsg.getParams().containsKey("AUTH")) {
                if (srvsMsg.getParams().get("AUTH").equals("true")) {
                    networkLogger.info("Сервер прислал сообщение об успешной регистрации.");
//                    nickName = srvsMsg.getParams().get("nickName");
                    setNickName(srvsMsg.getParams().get("nickName"));
                    controller.logIn();
                }
                if (!srvsMsg.getParams().get("AUTH").equals("true")) {
                    // отображение неуспешной регистрации
                }
            } else {
                for (Map.Entry<String, String> e : srvsMsg.getParams().entrySet()) {
                    switch (e.getKey()) {
                        case "nickName":
//                            nickName = e.getValue();
                            setNickName(e.getValue());
                            break;
                    }

                }
            }
        }

        if (obj instanceof ChatMsg) {
            ChatMsg msg = ((ChatMsg) obj);
            StringBuilder builder = new StringBuilder();
            builder.append(msg.getNickName()).append(": ").append(msg.getMessage());
            String textMesssage = builder.toString();
            controller.receiveMsg(textMesssage);
            historyLog(textMesssage);
        }
    }

    private void historyLog(String msg) {
        //test of existing history file
        if (!history.exists()) {
            try {
                history.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //writing from stringbuilder to file

        try {
            InputStream is = new ByteArrayInputStream(msg.getBytes(StandardCharsets.UTF_8));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(history, true));
            int i;
            while ((i = is.read()) != -1) {
                bos.write(i);
            }
            bos.write("\n".getBytes(StandardCharsets.UTF_8));
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setNickName(String nickName) {
        this.nickName = nickName;
        history = new File("client/src/main/resources/" + nickName + ".dat");
    }

    public String loadHistory() {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(history))) {
            Long skipping = (history.getTotalSpace()<=2048L)? 0 : history.getTotalSpace()-2048L;
            int i;
            StringBuilder sb = new StringBuilder();
            bis.skip(skipping);
            while ((i=bis.read()) != -1) {
                sb.append((char)i);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
            return null;
    }
}
