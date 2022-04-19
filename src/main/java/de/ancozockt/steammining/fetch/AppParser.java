package de.ancozockt.steammining.fetch;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.ancozockt.steammining.dataclasses.App;
import de.ancozockt.steammining.utility.APIRequest;
import lombok.Getter;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AppParser {

    private ArrayList<App> apps;
    private final String URL_STRING = "https://api.steampowered.com/ISteamApps/GetAppList/v0002/";
    private final List<String> TARGET_WORDS = List.of("Playtest", "Test Server", "Dedicated Server", "Server", "Soundtrack", "Tutorial", "DLC", "Demo", "Pack", "Map", "Artbook", "test");

    public AppParser(){
        apps = new ArrayList<>();

        try {
            fetch();
            filter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fetch() throws IOException {
        URL url = new URL(URL_STRING);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        APIRequest apiRequest = new APIRequest(connection);
        if(apiRequest.getStatus() == 200) {
            JsonObject object = apiRequest.getContentAsJsonObject();
            JsonArray appList = object.get("applist").getAsJsonObject().get("apps").getAsJsonArray();

            appList.forEach(element -> {
                JsonObject appObject = element.getAsJsonObject();

                if(appObject.has("appid")
                        && appObject.has("name")) {
                    if(appObject.get("name") != null) {
                        apps.add(App.builder()
                                .appId(appObject.get("appid").getAsInt())
                                .name(appObject.get("name").getAsString())
                                .build());
                    }
                }
            });
        }
    }


    private void filter(){
        apps = (ArrayList<App>) apps.stream().filter(app -> {
            if(app.getName().length() <= 1)
                return false;
            for(String word : TARGET_WORDS){
                if(app.getName().contains(word)){
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

}
