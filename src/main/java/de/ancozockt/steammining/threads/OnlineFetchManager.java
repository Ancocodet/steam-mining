package de.ancozockt.steammining.threads;

import de.ancozockt.steammining.dataclasses.App;
import de.ancozockt.steammining.dataclasses.Game;
import de.ancozockt.steammining.dataclasses.GamePlayers;
import de.ancozockt.steammining.fetch.OnlineParser;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class OnlineFetchManager {

    private int maxThreads;
    private int timeouts;

    private ArrayDeque<App> queue;
    @Getter
    private HashMap<Integer, Game> games;
    @Getter
    private ArrayList<GamePlayers> gamePlayers;

    public OnlineFetchManager(ArrayList<App> apps, int threads, int timeouts){
        queue = new ArrayDeque<>(apps);
        gamePlayers = new ArrayList<>();
        games = new HashMap<>();

        maxThreads = threads;
        this.timeouts = timeouts;

        startThreads();
    }

    private void startThreads(){
        for(int i = 0; i < maxThreads; i++){
            startNewThread();
        }
    }

    private void startNewThread(){
        if(!queue.isEmpty()) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(100 + new Random().nextInt(901));
                } catch (InterruptedException ignored) { }
                App app = queue.poll();
                if(app != null){
                    System.out.println("Thread started: " + app.getAppId() + " (remaining: " + queue.size() + ")");
                    try {
                        OnlineParser onlineParser = new OnlineParser(app.getAppId());
                        if (onlineParser.getCurrentPlayers() != -1) {
                            if (!games.containsKey(app.getAppId())) {
                                games.put(app.getAppId(), Game.fromApp(app));
                            }
                            Game game = games.getOrDefault(app.getAppId(), Game.fromApp(app));
                            gamePlayers.add(new GamePlayers(game, onlineParser.getCurrentPlayers()));
                        }
                    } catch (IOException exception) {
                        exception.printStackTrace();
                        queue.add(app);
                    } finally {
                        try {
                            Thread.sleep(timeouts);
                        } catch (InterruptedException ignored) {
                        }
                        startNewThread();
                    }
                }else{
                    startNewThread();
                }
            });
            thread.start();
        }
    }
}
