package lev.philippov;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;

@Getter
@Setter
public class Network {

    private Controller controller;
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private Logger networkLogger;


    public Network(Controller controller) {
        this.controller=controller;
        networkLogger = LoggerFactory.getLogger(this.getClass().getName());
        networkLogger.info("Network logger is ready!");
        initiateConnection();
    }


    public void initiateConnection() {
        try {
            this.socket = new Socket("localhost", 8189);
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(() -> {
                    try {
                        while(true) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
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
}
