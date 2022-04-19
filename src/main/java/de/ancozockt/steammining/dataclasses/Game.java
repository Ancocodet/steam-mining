package de.ancozockt.steammining.dataclasses;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    private String name;
    private int appId;

    public static Game fromApp(App app){
        return new Game(app.getName(), app.getAppId());
    }
}
