package de.ancozockt.steammining.threads;

import de.ancozockt.steammining.database.MySQLHandler;
import de.ancozockt.steammining.dataclasses.App;
import de.ancozockt.steammining.dataclasses.Game;
import de.ancozockt.steammining.dataclasses.GameDetails;
import de.ancozockt.steammining.fetch.DetailParser;
import lombok.Getter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DetailsFetchManager {

    private final MySQLHandler mySQLHandler;
    private final int maxThreads;
    private final int timeouts;
    private final int appsPerParse;

    private ArrayDeque<App> queue;
    @Getter
    private HashMap<Integer, Game> games;
    @Getter
    private ArrayList<GameDetails> gameDetails;

    public DetailsFetchManager(ArrayList<App> apps, MySQLHandler mySQLHandler,
                              int threads, int timeouts, int appsPerParse){
        queue = new ArrayDeque<>(apps);
        gameDetails = new ArrayList<>();
        games = new HashMap<>();

        maxThreads = threads;
        this.timeouts = timeouts;
        this.appsPerParse = appsPerParse;
        this.mySQLHandler = mySQLHandler;

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
                ArrayList<App> apps = new ArrayList<>();
                while (!queue.isEmpty() && apps.size() < appsPerParse){
                    App app = queue.poll();
                    if(app != null)
                        apps.add(queue.poll());
                }
                if(apps.size() > 0){
                    System.out.println("Thread started (remaining: " + queue.size() + ")");
                    try {
                        DetailParser detailParser = new DetailParser(apps);
                        detailParser.getDataMap().forEach((app, details) -> {
                            if(details.isSuccess()){
                                if(!mySQLHandler.hasGame(app.getAppId())){
                                    mySQLHandler.insertGame(app.getAppId(), app.getName());
                                }
                                if (!games.containsKey(app.getAppId())) {
                                    games.put(app.getAppId(), Game.fromApp(app));
                                }
                                Game game = games.getOrDefault(app.getAppId(), Game.fromApp(app));
                                mySQLHandler.insertData(app.getAppId(),
                                        details.getInitialPrice(),
                                        details.getFinalPrice(),
                                        details.getDiscountPercent()
                                );
                                gameDetails.add(details);
                            }
                        });
                    } catch (IOException exception) {
                        exception.printStackTrace();
                        queue.addAll(apps);
                    } finally {
                        try {
                            Thread.sleep(timeouts);
                        } catch (InterruptedException ignored) { }
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
