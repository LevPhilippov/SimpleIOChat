package lev.philippov;

import lev.philippov.Models.Client;
import lev.philippov.Services.AuthJDBCService;
import lev.philippov.Services.AuthService;
import lev.philippov.Services.SelfListeningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

    private final Vector<ClientHandler> clientHandler;

//    private ConcurrentHashMap<String, String> simpleAuthData;

    private AuthService authService;
    private final AuthJDBCService authJDBCService;

    Logger log;

    public Server() {
        this.clientHandler = new Vector<>();
//        this.simpleAuthData = new ConcurrentHashMap<>();
//        this.authService = new AuthService();
        this.authJDBCService = new AuthJDBCService();
        new SelfListeningService(this);
        this.log=LoggerFactory.getLogger(this.getClass().getName());
        log.info("Logger is ready.");
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            log.info("The server is ready and waiting for connections.");
             while (true) {
                 Socket socket = serverSocket.accept();
                 log.info("Client is connected to server");
                 new ClientHandler(this, socket);
             }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("Server closed");
        }
    }


    public void subscribeClientToServer(ClientHandler cl) {
        clientHandler.add(cl);
        log.trace("Client " + cl.getClient().getName() + " subscribed");
    }
    public void unsubscribeClientToServer(ClientHandler cl) {
        clientHandler.remove(cl);
        log.trace("Client " + cl.getClient().getName() + " unsubscribed");
    }


    public boolean checkReg(AuthMsg msg, ClientHandler clientHandler) {
        Client client = authJDBCService.authorizeClient(msg);
        if(client==null) {
            this.log.info("Клиент с логином " + msg.getLogin() + " не найден!");
            return false;
        }
        clientHandler.setClient(client);
        subscribeClientToServer(clientHandler);
        clientHandler.setAuthenticated();
        this.log.info("Клиент с логином " + msg.getLogin() + "успешно вошел в чат и подписан на рассылку!");
        return true;
    }


    public void broadcast(ChatMsg obj) {
        for (ClientHandler handler : clientHandler) {
            handler.msgSender(ClientHandler.MSG, obj, null);
        }
    }

    public void disconnectFromServer(String name) {
        ClientHandler handlerForDisconnect=null;
        for (ClientHandler handler : clientHandler) {
            if(handler.getClient().getName().equals(name)){
                handlerForDisconnect=handler;
                break;
            }
        }
        if(handlerForDisconnect !=null) handlerForDisconnect.disconnect();
    }

}
