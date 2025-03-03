package com.StockManager.Entities;

import com.StockManager.DAO.IngredientCrudRequests;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor@NoArgsConstructor@EqualsAndHashCode@Getter@Setter
public class Ingredient {
    private String ingredientId;
    private String name;
    private LocalDateTime updateDatetime;
    private List<Price> prices = new ArrayList<>();
    private List<StockMove> stockMoves = new ArrayList<>();

    public Price getPriceAtDate(LocalDate date){
        return this.prices.stream().filter(price -> price.getDate().equals(date)).findAny().orElseThrow(()-> new RuntimeException(" date fopla"));
    }
    public Price getPriceAtDate(){
        return this.prices.stream().sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate())).toList().getLast();
    }

    public StockMove getStockQuantity(LocalDateTime date){
        IngredientCrudRequests ingredientCrudRequests = new IngredientCrudRequests();
        String ingredientId = this.getIngredientId();
        List<StockMove> incomingStockMoves = ingredientCrudRequests.getAllStockMovesByIngredientId(ingredientId, MoveType.inComing, date);
        List<StockMove> outComingStockMoves = ingredientCrudRequests.getAllStockMovesByIngredientId(ingredientId, MoveType.outComing, date);
        double incomingStockIngredient = incomingStockMoves.stream().mapToDouble(stockmove -> stockmove.getQuantity()).sum();
        double outcomingStockIngredient = outComingStockMoves.stream().mapToDouble(stockmove -> stockmove.getQuantity()).sum();
        double totalStockIngredient = incomingStockIngredient - outcomingStockIngredient;
        StockMove stockQuantity = new StockMove();
        stockQuantity.setQuantity(totalStockIngredient);
        stockQuantity.setMoveDate(date);
        stockQuantity.setUnit(this.getPriceAtDate().getUnit());

        return stockQuantity;
    }


    @Override
    public String toString() {
        return
                " -Id :'" + ingredientId + '\n' +
                "name : " + name + '\n' +
                "updateDatetime : " + updateDatetime +
                "\nprices=" + prices ;
    }
}
