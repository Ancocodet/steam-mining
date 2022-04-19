package de.ancozockt.steammining.fetch;

import com.google.gson.JsonObject;
import de.ancozockt.steammining.utility.APIRequest;
import lombok.Getter;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@Getter
public class OnlineParser {

    private final String URL_STRING = "https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v0001?appId=";

    private long currentPlayers = -1;

    public OnlineParser(int appId) throws IOException {
        URL url = new URL(URL_STRING + appId);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        APIRequest apiRequest = new APIRequest(connection);
        if(apiRequest.getStatus() == 200) {
            JsonObject object = apiRequest.getContentAsJsonObject();
            currentPlayers = object.get("response").getAsJsonObject().get("player_count").getAsLong();
        }
    }

}
