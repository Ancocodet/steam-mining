package de.ancozockt.steammining.threads;

import de.ancozockt.steammining.dataclasses.App;
import de.ancozockt.steammining.dataclasses.Game;
import de.ancozockt.steammining.dataclasses.GamePlayers;
import de.ancozockt.steammining.fetch.OnlineParser;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class OnlineFetchManager {

    private int maxThreads;
    private int timeouts;

    private ArrayDeque<App> queue;
    private HashMap<Integer, Game> games;
    private ArrayList<GamePlayers> gamePlayers;

    public OnlineFetchManager(ArrayList<App> apps, int threads, int timeouts){
        queue = new ArrayDeque<>(apps);
        gamePlayers = new ArrayList<>();

        maxThreads = threads;
        this.timeouts = timeouts;
    }

    private void startThreads(){
        for(int i = 0; i < maxThreads; i++){
            startNewThread();
        }
    }

    private void startNewThread(){
        Thread thread = new Thread(() -> {
            App app = queue.poll();
            try {
                OnlineParser onlineParser = new OnlineParser(app.getAppId());
                if(onlineParser.getCurrentPlayers() != -1){
                    if(!games.containsKey(app.getAppId())){
                        games.put(app.getAppId(), Game.fromApp(app));
                    }
                    Game game = games.get(app.getAppId());
                    gamePlayers.add(new GamePlayers(game, onlineParser.getCurrentPlayers()));
                }
            }catch (IOException exception){
                queue.add(app);
            } finally {
                try {
                    Thread.sleep(timeouts);
                } catch (InterruptedException ignored) { }
                startNewThread();
            }
        });
        thread.start();
    }
}
