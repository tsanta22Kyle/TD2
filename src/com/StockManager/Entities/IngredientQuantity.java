package com.StockManager.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter@AllArgsConstructor@NoArgsConstructor@Setter
public class IngredientQuantity {
    Ingredient ingredient;
    double quantity;
    unit unit;

    public double getTotalCost(LocalDate date){
        return quantity*ingredient.getPriceAtDate(date).getValue();
    }
    public double getTotalCost(){
        return quantity*ingredient.getPriceAtDate().getValue();
    }


    @Override
    public String toString() {
        return "\n " + ingredient +
                "\n quantity : " + this.quantity +
                " " + unit ;
    }
}
