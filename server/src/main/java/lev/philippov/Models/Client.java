package lev.philippov.Models;

import lombok.Data;

import java.util.Objects;

@Data
public class Client {

    private long id;
    private String login;
    private String password;
    private String nickName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(login, client.login) && Objects.equals(password, client.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password);
    }
}
