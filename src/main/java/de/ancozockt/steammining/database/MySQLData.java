package de.ancozockt.steammining.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MySQLData {

    private String hostName = "localhost";
    private int port = 3306;

    private String database = "database";

    private String username = "username";
    private String password = "password";

    private File file;

    public MySQLData(File file){
        this.file = file;

        if(file.exists()){
            read();
        }else{
            write();
        }
    }

    public void write(){
        try {
            JsonObject object = new JsonObject();

            object.addProperty("hostName", "localhost");
            object.addProperty("port", 3306);
            object.addProperty("database", "database");
            object.addProperty("username", "username");
            object.addProperty("password", "password");

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter =new OutputStreamWriter(fOut);

            gson.toJson(object, myOutWriter);

            myOutWriter.close();
            fOut.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void read(){
        try {
            String content = new String(Files.readAllBytes(Paths.get(file.getPath()))).replace("\\\n","").replace( "\\", "");
            JsonElement element = new Gson().fromJson(content, JsonObject.class);

            hostName = element.getAsJsonObject().get("hostName").getAsString();
            port = element.getAsJsonObject().get("port").getAsInt();
            database = element.getAsJsonObject().get("database").getAsString();

            username = element.getAsJsonObject().get("username").getAsString();
            password = element.getAsJsonObject().get("password").getAsString();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}