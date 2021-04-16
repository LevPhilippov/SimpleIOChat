package lev.philippov.Interfaces;

import lev.philippov.Models.Client;

public interface ClientRepository {
    Client getClientByLogin(String login);
    Client getClientByLoginAndPassword(String login, String password);
}
