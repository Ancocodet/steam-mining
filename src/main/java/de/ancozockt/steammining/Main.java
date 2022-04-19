package de.ancozockt.steammining;

import de.ancozockt.steammining.dataclasses.App;
import de.ancozockt.steammining.dataclasses.Game;
import de.ancozockt.steammining.dataclasses.GameDetails;
import de.ancozockt.steammining.dataclasses.GamePlayers;
import de.ancozockt.steammining.fetch.AppParser;
import de.ancozockt.steammining.fetch.OnlineParser;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        AppParser appParser = new AppParser();

        HashMap<Integer, Game> games = new HashMap<>();
        List<GamePlayers> gamePlayers = new ArrayList<>();

        ArrayDeque<App> queue = new ArrayDeque<>(appParser.getApps());

        for(App app : appParser.getApps()){
            try {
                OnlineParser onlineParser = new OnlineParser(app.getAppId());
                if(onlineParser.getCurrentPlayers() != -1){
                    if(!games.containsKey(app.getAppId())){
                        games.put(app.getAppId(), Game.fromApp(app));
                    }
                    Game game = games.get(app.getAppId());
                    gamePlayers.add(new GamePlayers(game, onlineParser.getCurrentPlayers()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
