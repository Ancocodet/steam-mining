package de.ancozockt.steammining.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MySQLHandler {

    private MySQL mySQL;

    public MySQLHandler(MySQL mySQL){
        this.mySQL = mySQL;
        createTables();
    }

    private void createTables(){
        mySQL.update("CREATE TABLE IF NOT EXISTS games (appid INT PRIMARY KEY, name TEXT);");

        mySQL.update("CREATE TABLE IF NOT EXISTS games_data (uuid VARCHAR(100) PRIMARY KEY, game_id INT, created DATETIME, initial_price INT, final_price INT, discount_percent INT, FOREIGN KEY (game_id) REFERENCES games(appid));");
        mySQL.update("CREATE TABLE IF NOT EXISTS games_online (uuid VARCHAR(100) PRIMARY KEY, game_id INT, created DATETIME, online_players LONG, FOREIGN KEY (game_id) REFERENCES games(appid));");
    }

    public boolean hasGame(int appId){
        try {
            PreparedStatement preparedStatement = mySQL.getConnection()
                    .prepareStatement("SELECT * FROM games WHERE appid=?");
            preparedStatement.setInt(1, appId);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }catch (SQLException exception){
            exception.printStackTrace();
        }
        return false;
    }

    public void insertGame(int appId, String name){
        if(!hasGame(appId)){
            try {
                PreparedStatement preparedStatement = mySQL.getConnection()
                        .prepareStatement("INSERT INTO games (appid, name) VALUES (?,?);");

                preparedStatement.setInt(1, appId);
                preparedStatement.setString(2, name);

                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void insertData(int appId, int initialPrice, int finalPrice, int discountPercent){
        if(hasGame(appId)){
            UUID uuid = UUID.randomUUID();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                PreparedStatement preparedStatement = mySQL.getConnection()
                        .prepareStatement("INSERT INTO games_data (uuid, game_id, created," +
                                " initial_price, final_price, discount_percent) VALUES (?,?,?,?,?,?);");
                
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setInt(2, appId);
                preparedStatement.setString(3, format.format(new Date()));

                preparedStatement.setInt(4, initialPrice);
                preparedStatement.setInt(5, finalPrice);
                preparedStatement.setInt(6, discountPercent);

                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void insertPlayerCount(int appId, long onlinePlayers){
        if(hasGame(appId)){
            UUID uuid = UUID.randomUUID();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                PreparedStatement preparedStatement = mySQL.getConnection()
                        .prepareStatement("INSERT INTO games_online (uuid, game_id, created, online_players) VALUES (?,?,?,?);");

                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setInt(2, appId);
                preparedStatement.setString(3, format.format(new Date()));

                preparedStatement.setLong(4, onlinePlayers);

                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}
