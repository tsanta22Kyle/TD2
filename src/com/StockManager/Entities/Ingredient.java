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

    public StockMove getStockQuantityAt(LocalDateTime date){
        List<StockMove> incomingStockMoves = stockMoves.stream().filter(stockMove -> stockMove.getMoveType().equals(MoveType.inComing) & stockMove.getMoveDate() == date).toList();
        List<StockMove> outComingStockMoves = stockMoves.stream().filter(stockMove -> stockMove.getMoveType().equals(MoveType.outComing) & stockMove.getMoveDate() == date).toList();
        double incomingStockIngredient = incomingStockMoves.stream().mapToDouble(stockmove -> stockmove.getQuantity()).sum();
        double outcomingStockIngredient = outComingStockMoves.stream().mapToDouble(stockmove -> stockmove.getQuantity()).sum();
        double totalStockIngredient = incomingStockIngredient - outcomingStockIngredient;
        StockMove stockQuantity = new StockMove();
        stockQuantity.setQuantity(totalStockIngredient);
        stockQuantity.setMoveDate(date);
        stockQuantity.setUnit(this.getPriceAtDate().getUnit());

        return stockQuantity;
    }
    public StockMove getStockQuantityAt(){
        List<StockMove> incomingStockMoves = stockMoves.stream().filter(stockMove -> stockMove.getMoveType().equals(MoveType.inComing) & stockMove.getMoveDate() == LocalDateTime.now()).toList();
        List<StockMove> outComingStockMoves = stockMoves.stream().filter(stockMove -> stockMove.getMoveType().equals(MoveType.outComing) & stockMove.getMoveDate() == LocalDateTime.now()).toList();
        double incomingStockIngredient = incomingStockMoves.stream().mapToDouble(stockmove -> stockmove.getQuantity()).sum();
        double outcomingStockIngredient = outComingStockMoves.stream().mapToDouble(stockmove -> stockmove.getQuantity()).sum();
        double totalStockIngredient = incomingStockIngredient - outcomingStockIngredient;
        StockMove stockQuantity = new StockMove();
        stockQuantity.setQuantity(totalStockIngredient);
        //stockQuantity.setMoveDate();
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
