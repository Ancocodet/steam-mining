package de.ancozockt.steammining.dataclasses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameDetails {

    private boolean success;
    private int appId;

    private int discountPercent;
    private int initialPrice;
    private int finalPrice;

}
