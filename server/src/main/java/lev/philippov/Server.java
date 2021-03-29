package lev.philippov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

    private Vector<ClientHandler> clientHandler;
    Logger log;
    public Server() {
        clientHandler = new Vector<>();
        this.log=LoggerFactory.getLogger(this.getClass().getName());
        log.info("Logger is ready.");
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            log.info("The server is ready and waiting for connections.");
             while (true) {
                 Socket socket = serverSocket.accept();
                 subscribeClientToServer(new ClientHandler(this, socket));
                 log.info("Client is connected to server");
             }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("Server closed");
        }
    }


    public void subscribeClientToServer(ClientHandler cl) {
        clientHandler.add(cl);
        log.trace("Client subscribed");
    }

    public void subscribeClientFromServer (ClientHandler cl) {
        clientHandler.remove(cl);
        log.trace("Client unsubscribed");
    }
}
