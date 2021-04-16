package lev.philippov.Services;

import lev.philippov.AuthMsg;
import lev.philippov.Models.Client;
import lev.philippov.ClientRepositoryImpl;
import lev.philippov.Interfaces.ClientRepository;

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


}
