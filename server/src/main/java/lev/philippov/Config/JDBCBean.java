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
    private static String url;
    private static String user;
    private static String password;

    public JDBCBean() {
        try {
            Class.forName("org.postgresql.Driver");
            setDBAccessData();
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeUpdate(readBDConstructionFile());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            logger.error("DB isn't runned!\n" + e.getMessage());
        }
    }

    private void setDBAccessData() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("DBAccessData.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(is))
        ) {
            while (br.ready()) {
                String line = br.readLine();
                String[] lines = line.split("\\s", 2);

                switch (lines[0]) {
                    case ("url"):
                        url = lines[1];
                        break;
                    case ("user"):
                        user = lines[1];
                        break;
                    case ("password"):
                        password = lines[1];
                        break;
                }
            }
        } catch (IOException e) {
            logger.error("Не удалось войти в базу данных!\n" + e.getMessage());
        }
        System.out.println(url);
        System.out.println(user);
        System.out.println(password);
    }

    private String readBDConstructionFile() {
//        String root = Thread.currentThread().getContextClassLoader().getResource("DBconstruction.txt").toString();
//        File file = new File("");
        StringBuilder builder = new StringBuilder();
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("DBconstruction.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        ) {
            while (bufferedReader.ready()) {
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
            preparedStatement.setString(1, login);
            return preparedStatement.executeQuery();
        } catch (SQLException throwables) {
            logger.error(throwables.getMessage());
        }
        return null;
    }

    public ResultSet getClientResultSetByLoginAndPassword(String login, String password) {
        try {
            preparedStatement = connection.prepareStatement("SELECT login, password, name FROM chatdb WHERE login=? and password=?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            return preparedStatement.executeQuery();
        } catch (SQLException throwables) {
            logger.error(throwables.getMessage());
        }
        return null;
    }

}
