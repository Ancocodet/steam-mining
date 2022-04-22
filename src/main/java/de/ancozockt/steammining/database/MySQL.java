package de.ancozockt.steammining.database;

import java.sql.*;

public class MySQL {

    private Connection connection;

    public MySQL(MySQLData data){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + data.getHostName() + ":" + data.getPort() + "/" + data.getDatabase() + "?autoReconnect=true&characterEncoding=utf8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin", data.getUsername(), data.getPassword());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public ResultSet query(String query){
        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public void update(String update){
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(update);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
