package lev.philippov;

import lev.philippov.Config.JDBCBean;
import lev.philippov.Interfaces.ClientRepository;
import lev.philippov.Models.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientRepositoryImpl implements ClientRepository {

    private JDBCBean jdbcBean;
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public ClientRepositoryImpl() {
        this.jdbcBean = new JDBCBean();
    }

    @Override
    public Client getClientByLogin(String login) {
        ResultSet resultSet = jdbcBean.getClientResultSetByLogin(login);
        try{
        if(resultSet.next()){
                String password = resultSet.getString("password");
                String name = resultSet.getString("name");
                Client client = new Client();
                client.setLogin(login);
                client.setPassword(password);
                client.setNickName(name);
                return client;
            }} catch (SQLException e){
                logger.error("Ошибка в интерпретировании ResultSet\n" + e.getMessage());
            }
        return null;
    }

    @Override
    public Client getClientByLoginAndPassword(String login, String password) {
        ResultSet resultSet = jdbcBean.getClientResultSetByLoginAndPassword(login, password);
        try{
            if(resultSet.next()) {
                Client client = new Client();
                client.setLogin(login);
                client.setPassword(password);
                client.setNickName(resultSet.getString("name"));
                return client;
            }
        } catch (SQLException e){
                logger.error("Ошибка в интерпретировании ResultSet\n" + e.getMessage());
            }
        return null;
    }
    @Override
    public int changeNickNameByClient(Client client, String nickname) {
        return jdbcBean.changeNickname(client, nickname);
    }
}
