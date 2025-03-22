package com.StockManager.Entities;

import com.StockManager.DAO.DishCrudRequests;
import com.StockManager.DAO.IngredientCrudRequests;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
public class Dish {
    private String dishId;
    private String name;
    private int unitPrice;
    private List<IngredientQuantity> ingredientList = new ArrayList<>();

    public Dish(String dishId, String name, int unitPrice, double quantity, Ingredient baseIngredient, unit unit) {
        this.dishId = dishId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.ingredientList.add(new IngredientQuantity(baseIngredient, quantity, unit));
    }

    public void addOneIngredient(Ingredient ingredient, double quantity, unit unit) {
        this.ingredientList.add(new IngredientQuantity(ingredient, quantity, unit));
    }

    /* public void addManyIngredient(List<Ingredient> ingredientsToAdd){
         ingredientsToAdd.stream().forEach(ingredient -> this.ingredientList.add(ingredient));
     }*/
    public void removeIngredient(String id) {
        this.ingredientList = this.ingredientList.stream().filter(ingredient -> ingredient.getIngredient().getIngredientId() != id).toList();
    }

    public double getIngredientToTalCost(LocalDate date) {

        return this.ingredientList.stream().mapToDouble(ingredient -> ingredient.getTotalCost(date)).sum();
    }
    public double getIngredientToTalCost() {

        return this.ingredientList.stream().mapToDouble(ingredient -> ingredient.getTotalCost()).sum();
    }
    public double getGrossMargin(){
        return this.getUnitPrice()-this.getIngredientToTalCost();
    }

    public double getGrossMargin(LocalDate date){
        return this.getUnitPrice()-this.getIngredientToTalCost(date);
    }
    public double getAvailableQuantity(LocalDateTime dateTime){

        List<Double> quantities = new ArrayList<>();

         ingredientList.forEach(ingredientQuantity -> {
             if(ingredientQuantity.getIngredient().getStockQuantityAt(dateTime).getQuantity()==0){
                quantities.add(0.0);
             }else {

          double quantity =  ingredientQuantity.getQuantity()/ingredientQuantity.getIngredient().getStockQuantityAt(dateTime).getQuantity();
             quantities.add(quantity);
             }
        });
        if (quantities.isEmpty()) {
            return 0.0;
        }

        return Math.round(quantities.stream().sorted((o1, o2) -> o1.compareTo(o2)).toList().get(0));


    }
    public double getAvailableQuantity(){
        List<Double> quantities = new ArrayList<>();

        ingredientList.forEach(ingredientQuantity -> {
            if(ingredientQuantity.getIngredient().getStockQuantityAt().getQuantity()==0){
                quantities.add(0.0);
            }else {

                double quantity =  ingredientQuantity.getQuantity()/ingredientQuantity.getIngredient().getStockQuantityAt().getQuantity();
                quantities.add(quantity);
            }
        });
        if (quantities.isEmpty()) {
            return 0.0;
        }

        return Math.round(quantities.stream().sorted((o1, o2) -> o1.compareTo(o2)).toList().get(0));


    }

    @Override
    public String toString() {
        return "- " + this.name + " \nid : " + this.dishId + "\nprice : " + this.unitPrice + " \n ingredients : " + " \n" + " " + this.ingredientList.toString();
    }


}
