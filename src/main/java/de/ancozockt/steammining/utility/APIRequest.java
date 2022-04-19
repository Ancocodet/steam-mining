package de.ancozockt.steammining.utility;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class APIRequest {

    private StringBuffer content;
    private int status;

    public APIRequest(HttpsURLConnection connection) throws IOException {
        connection.connect();

        status = connection.getResponseCode();

        if(status == 200) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        }
    }

    public String getContent() {
        return String.valueOf(content);
    }

    public JsonObject getContentAsJsonObject(){
        return new Gson().fromJson(String.valueOf(content), JsonObject.class);
    }

    public int getStatus() {
        return status;
    }

}
