package lev.philippov;

import java.util.concurrent.ConcurrentHashMap;

public class AuthService implements ClientRepository {

    private ConcurrentHashMap<String, Client> simpleAuthData;

    public AuthService() {
        this.simpleAuthData = new ConcurrentHashMap<>();
        Client client = new Client();
        client.setLogin("name1");
        client.setPassword("12345");
        client.setName("Dallas");
        simpleAuthData.put(client.getLogin(), client);
    }

    @Override
    public Client getClientFromLogin(String login) {
        return simpleAuthData.get(login);
    }

    public boolean checkRegistrationData(AuthMsg msg) {
        Client client;
        if ((client = getClientFromLogin(msg.getLogin())) == null) return false;
        return client.getPassword().equals(msg.getPassword());
    }


}
