package de.ancozockt.steammining;

import de.ancozockt.steammining.database.MySQL;
import de.ancozockt.steammining.database.MySQLData;
import de.ancozockt.steammining.database.MySQLHandler;
import de.ancozockt.steammining.fetch.AppParser;
import de.ancozockt.steammining.threads.OnlineFetchManager;
import de.ancozockt.steammining.utility.Config;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        if(args.length > 0 && args[0].equalsIgnoreCase("--full")){
            fullFetch();
        }else{
            onlineFetch();
        }
    }

    private static void fullFetch(){
        AppParser appParser = new AppParser();

        System.out.println(appParser.getApps().size());
    }

    private static void onlineFetch(){
        Config config = new Config(new File("config.json"));
        MySQLHandler mySQLHandler = connectToDatabase();

        AppParser appParser = new AppParser();

        System.out.println(appParser.getApps().size());

        OnlineFetchManager fetchManager = new OnlineFetchManager(appParser.getApps(),
                config.getThreads(), config.getTimeouts());

        fetchManager.getGames().forEach((integer, game) -> {
            if(mySQLHandler.hasGame(game.getAppId()))
                mySQLHandler.insertGame(game.getAppId(), game.getName());
        });
        System.out.println(fetchManager.getGamePlayers().size());
    }

    private static MySQLHandler connectToDatabase(){
        MySQLData mySQLData = new MySQLData(new File("mysql.json"));
        MySQL mySQL = new MySQL(mySQLData);
        return new MySQLHandler(mySQL);
    }
}
