package lev.philippov.Services;

import lev.philippov.AuthMsg;
import lev.philippov.Models.Client;
import lev.philippov.ClientRepositoryImpl;
import lev.philippov.Interfaces.ClientRepository;
import lev.philippov.SrvsMsg;

import java.util.Map;

public class AuthJDBCService {

    ClientRepository repository;

    public AuthJDBCService() {
        repository = new ClientRepositoryImpl();
    }

//    public Client getClientFromLogin(String login) {
//        return repository.getClientByLogin(login);
//    }

    public Client authorizeClient(AuthMsg msg) {
        return repository.getClientByLoginAndPassword(msg.getLogin(), msg.getPassword());
    }
    public Client authorizeClient(SrvsMsg msg) {
        return repository.getClientByLoginAndPassword(msg.getParams().get("login"), msg.getParams().get("password"));
    }

    public int changeNicknameByClient (Client client, String nickname) {
        return repository.changeNickNameByClient(client, nickname);
    }

}
