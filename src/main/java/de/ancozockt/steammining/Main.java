package de.ancozockt.steammining;

import de.ancozockt.steammining.database.MySQL;
import de.ancozockt.steammining.database.MySQLData;
import de.ancozockt.steammining.database.MySQLHandler;
import de.ancozockt.steammining.fetch.AppParser;
import de.ancozockt.steammining.threads.DetailsFetchManager;
import de.ancozockt.steammining.threads.OnlineFetchManager;
import de.ancozockt.steammining.utility.Config;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        if(args.length > 0 && args[0].equalsIgnoreCase("--details")){
            detailsFetch();
        }else{
            onlineFetch();
        }
    }

    private static void detailsFetch(){
        Config config = new Config(new File("config.json"));
        MySQLHandler mySQLHandler = connectToDatabase();

        AppParser appParser = new AppParser();

        DetailsFetchManager fetchManager = new DetailsFetchManager(appParser.getApps(), mySQLHandler,
                config.getThreads(), config.getTimeouts());
    }

    private static void onlineFetch(){
        Config config = new Config(new File("config.json"));
        MySQLHandler mySQLHandler = connectToDatabase();

        AppParser appParser = new AppParser();

        OnlineFetchManager fetchManager = new OnlineFetchManager(appParser.getApps(), mySQLHandler,
                config.getThreads(), config.getTimeouts());
    }

    private static MySQLHandler connectToDatabase(){
        MySQLData mySQLData = new MySQLData(new File("mysql.json"));
        MySQL mySQL = new MySQL(mySQLData);
        return new MySQLHandler(mySQL);
    }
}
