package lev.philippov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;


public class ClientHandler {
    private Server server;
    private Socket socket;
    private Logger logger;

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        this.logger = LoggerFactory.getLogger(this.getClass().getName());
        handleTheClient();
    }

    public void handleTheClient() {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            Thread t = new Thread(() -> {
                try {
                   while (true) {
                       int x;
                       while ((x=dis.read())!=-1) {
                           System.out.println(x);
                       }
//                       String message= dis.readUTF();
//                        if (message.equals("/end")) break;
//                       System.out.println(message);
                   }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                } finally {
                    closeConnection(dis);
                }
            });
            t.setDaemon(true);
            t.start();

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
}
