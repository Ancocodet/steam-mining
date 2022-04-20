package de.ancozockt.steammining.fetch;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ancozockt.steammining.dataclasses.App;
import de.ancozockt.steammining.dataclasses.GameDetails;
import de.ancozockt.steammining.utility.APIRequest;
import lombok.Getter;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DetailParser {

    private final String URL_STRING = "https://store.steampowered.com/api/appdetails?appids=%s&cc=de&l=en&filters=price_overview";

    private HashMap<App, GameDetails> dataMap;

    public DetailParser(List<App> apps) throws IOException {
        dataMap = new HashMap<>();

        List<String> appIds = apps.stream().map(app -> String.valueOf(app.getAppId())).collect(Collectors.toList());
        URL url = new URL(String.format(URL_STRING, String.join(",", appIds)));
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        APIRequest apiRequest = new APIRequest(connection);
        if(apiRequest.getStatus() == 200) {
            JsonObject object = apiRequest.getContentAsJsonObject();

            apps.forEach(app -> {
                GameDetails.GameDetailsBuilder detailsBuilder = GameDetails.builder();
                detailsBuilder = detailsBuilder.appId(app.getAppId());
                JsonObject appRequest = object.get(String.valueOf(app.getAppId())).getAsJsonObject();

                if(appRequest.get("success").getAsBoolean()){
                    JsonElement data = appRequest.get("data");

                    detailsBuilder = detailsBuilder.success(appRequest.get("success").getAsBoolean());

                    if(data.isJsonArray()){
                        detailsBuilder = detailsBuilder.finalPrice(0)
                                .discountPercent(0)
                                .initialPrice(0);
                    }else if(data.isJsonObject()){
                        JsonObject dataObject = data.getAsJsonObject();
                        if(dataObject.has("price_overview")){
                            JsonObject priceOverview = dataObject.get("price_overview").getAsJsonObject();

                            int initialPrice = priceOverview.get("initial").getAsInt();
                            int finalPrice = priceOverview.get("final").getAsInt();
                            int discount = priceOverview.get("discount_percent").getAsInt();

                            detailsBuilder = detailsBuilder
                                    .initialPrice(initialPrice)
                                    .finalPrice(finalPrice)
                                    .discountPercent(discount);
                        }
                    }
                    dataMap.put(app, detailsBuilder.build());
                }
            });
        }else{
            System.err.println("Server returned " + apiRequest.getStatus());
        }
    }

}
