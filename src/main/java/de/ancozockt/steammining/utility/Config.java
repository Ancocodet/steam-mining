package de.ancozockt.steammining.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

@Getter
public class Config {

    private int threads = 10;
    private int timeouts = 5000;

    private File file;

    public Config(File file){
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

            object.addProperty("threads", 10);
            object.addProperty("timeouts", 5000);

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

            threads = element.getAsJsonObject().get("threads").getAsInt();
            timeouts = element.getAsJsonObject().get("timeouts").getAsInt();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
