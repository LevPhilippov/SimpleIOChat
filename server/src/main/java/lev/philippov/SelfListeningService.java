package lev.philippov;

import java.io.*;

public class SelfListeningService {
    Server server;
    InputStreamReader reader = new InputStreamReader(System.in);
    BufferedReader bufferedReader = new BufferedReader(reader);

    public SelfListeningService(Server server) {
        this.server = server;

        Thread stdinListener = new Thread(() -> {

            while (true) {
                try {
                    readCommand(bufferedReader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        stdinListener.setDaemon(true);
        stdinListener.start();
    }

    private void readCommand(String command) {
        if(command.startsWith("/")){
            String[] tokens = command.split("\\s",2);
            switch (tokens[0]){
                case "/disconnect":
                    server.disconnectFromServer(tokens[1].trim());
                case "/broadcast":
                    System.out.println(tokens[1].trim());
                    server.broadcast(ChatMsg.builder().name("Server").message(tokens[1].trim()).build());
            }
        } else System.out.println("Не верная команда! Список доступных команд:\n/disconnect " +
                "[Name of client]\n/broadcast [Message you want to broarcast]");

    }
}
