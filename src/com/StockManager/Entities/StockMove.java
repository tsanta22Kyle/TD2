package com.StockManager.Entities;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StockMove {
    private MoveType moveType;
    private double quantity;
    private unit unit;
    private LocalDateTime moveDate;

    @Override
    public String toString() {
        return
                "--> moveType : " + moveType +
                        "\n quantity : " + quantity +
                          unit +
                        "\n moveDate : " + moveDate ;
    }
}
