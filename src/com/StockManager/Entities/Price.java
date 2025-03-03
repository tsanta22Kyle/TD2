package com.StockManager.Entities;

import lombok.*;

import java.security.Timestamp;
import java.time.LocalDate;

@Getter@EqualsAndHashCode@AllArgsConstructor@NoArgsConstructor@Setter
public class Price {
    private String id;
    private double value;
    private LocalDate date;
    private unit unit;




    @Override
    public String toString() {
        return
                " price : " + value +
                " at " + date +
                "unit : " + unit ;
    }
}
