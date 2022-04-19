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
        mySQL.update("CREATE TABLE IF NOT EXISTS genres (id INT PRIMARY KEY, genreid INT, name TEXT);");
        mySQL.update("CREATE TABLE IF NOT EXISTS games (id INT PRIMARY KEY, appid INT, name TEXT, release_date DATE);");

        mySQL.update("CREATE TABLE IF NOT EXISTS games_genres (uuid VARCHAR(100) PRIMARY KEY, game_id INT, genre_id INT, FOREIGN KEY (game_id) REFERENCES games(id), FOREIGN KEY (genre_id) REFERENCES genres(id));");
        mySQL.update("CREATE TABLE IF NOT EXISTS games_data (uuid VARCHAR(100) PRIMARY KEY, game_id INT, created DATETIME, price INT, recommendations LONG, FOREIGN KEY (game_id) REFERENCES games(id));");
        mySQL.update("CREATE TABLE IF NOT EXISTS games_online (uuid VARCHAR(100) PRIMARY KEY, game_id INT, created DATETIME, online_players LONG, FOREIGN KEY (game_id) REFERENCES games(id));");
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

    public boolean hasGenre(int genreId){
        try {
            PreparedStatement preparedStatement = mySQL.getConnection()
                    .prepareStatement("SELECT * FROM genres WHERE genreid=?");
            preparedStatement.setInt(1, genreId);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }catch (SQLException exception){
            exception.printStackTrace();
        }
        return false;
    }

    public void insertGenre(int genreId, String name){
        if(!hasGenre(genreId)){
            try {
                PreparedStatement preparedStatement = mySQL.getConnection()
                        .prepareStatement("INSERT INTO genres (genreid, name) VALUES (?,?);");
                preparedStatement.setInt(1, genreId);
                preparedStatement.setString(2, name);

                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void insertGame(int appId, String name, String date){
        if(!hasGame(appId)){
            try {
                PreparedStatement preparedStatement = mySQL.getConnection()
                        .prepareStatement("INSERT INTO games (appid, name, date) VALUES (?,?, STR_TO_DATE('" + date + "', 'dd MM, YY'));");
                preparedStatement.setInt(1, appId);
                preparedStatement.setString(2, name);

                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void addGenre(int appId, int genreId){
        if(hasGenre(genreId) && hasGame(appId)){
            try {
                PreparedStatement preparedStatement = mySQL.getConnection()
                        .prepareStatement("INSERT INTO games_genres (game_id, genre_id) VALUES (?, ?);");
                preparedStatement.setInt(1, appId);
                preparedStatement.setInt(2, genreId);

                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void insertData(int appId, int price, int rating){
        if(hasGame(appId)){
            UUID uuid = UUID.randomUUID();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                PreparedStatement preparedStatement = mySQL.getConnection()
                        .prepareStatement("INSERT INTO games_data (uuid, game_id, created, prices, recommendations) VALUES (?,?,?,?,?);");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setInt(2, appId);
                preparedStatement.setString(3, format.format(new Date()));

                preparedStatement.setInt(4, price);
                preparedStatement.setLong(5, rating);

                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void insertPlayerCount(int appId, int onlinePlayers){
        if(hasGame(appId)){
            UUID uuid = UUID.randomUUID();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                PreparedStatement preparedStatement = mySQL.getConnection()
                        .prepareStatement("INSERT INTO games_data (uuid, game_id, created, online_players) VALUES (?,?,?,?);");
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