package lev.philippov.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;


public class JDBCBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private static String url = "jdbc:postgresql://localhost:5432/postgres?currentSchema=lesson";
    private static String user = "postgres";
    private static String password = "admin";

    public JDBCBean() {


        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user , password);
            statement = connection.createStatement();
            statement.executeUpdate(readBDConstructionFile());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            logger.error("DB isn't runned!\n" + e.getMessage());
        }
    }

    private String readBDConstructionFile() {
//        String root = Thread.currentThread().getContextClassLoader().getResource("DBconstruction.txt").toString();
//        File file = new File("");
        StringBuilder builder = new StringBuilder();
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("DBconstruction.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        ) {
            while (bufferedReader.ready()){
                builder.append(bufferedReader.readLine());
            }
        } catch (IOException e) {
            logger.error("Не удалось прочесть файл реконструкции базы данных!\n" + e.getMessage());
        }
        return builder.toString();
    }

    public ResultSet getClientResultSetByLogin(String login) {
        try {
            preparedStatement = connection.prepareStatement("SELECT login, password, name FROM chatdb WHERE login=?");
            preparedStatement.setString(1,login);
            return preparedStatement.executeQuery();
        } catch (SQLException throwables) {
            logger.error(throwables.getMessage());
        }
        return null;
    }

    public ResultSet getClientResultSetByLoginAndPassword(String login, String password) {
        try {
            preparedStatement = connection.prepareStatement("SELECT login, password, name FROM chatdb WHERE login=? and password=?");
            preparedStatement.setString(1,login);
            preparedStatement.setString(2,password);
            return preparedStatement.executeQuery();
        } catch (SQLException throwables) {
            logger.error(throwables.getMessage());
        }
        return null;
    }

}
