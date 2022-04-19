package de.ancozockt.steammining.fetch;

import com.google.gson.JsonObject;
import de.ancozockt.steammining.utility.APIRequest;
import lombok.Getter;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;

@Getter
public class DetailParser {

    private final String URL_STRING = "https://store.steampowered.com/api/appdetails?appids=%d&cc=de&l=en";

    private boolean free = false;

    private int initialPrice = -1;
    private int finalPrice = -1;
    private int discount = 0;

    private long recommendations;

    private boolean success = false;

    public DetailParser(int appId) throws IOException {
        URL url = new URL(String.format(URL_STRING, appId));
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        APIRequest apiRequest = new APIRequest(connection);
        if(apiRequest.getStatus() == 200) {
            JsonObject object = apiRequest.getContentAsJsonObject();

            JsonObject appRequest = object.get(String.valueOf(appId)).getAsJsonObject();

            if(appRequest.get("success").getAsBoolean()){
                JsonObject data = appRequest.get("data").getAsJsonObject();

                String type = data.get("type").getAsString();
                if(type.equalsIgnoreCase("game")){
                    success = true;

                    free = data.get("is_free").getAsBoolean();
                    recommendations = data.get("recommendations").getAsJsonObject().get("total").getAsLong();
                    if(!free){
                        JsonObject priceOverview = data.get("price_overview").getAsJsonObject();

                        initialPrice = priceOverview.get("initial").getAsInt();
                        finalPrice = priceOverview.get("final").getAsInt();
                        discount = priceOverview.get("discount_percent").getAsInt();
                    }
                }
            }
        }
    }

}
