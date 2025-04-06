package com.StockManager.Entities;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
@Setter
public class DishOrder {
    private String dishOrderId;
    private int quantity;
    private List<OrderStatus> statusList = new ArrayList<>();
    private Dish dish;


    //une commande de plat zany ty
    public DishOrder(String dishOrderId, int quantity, List<OrderStatus> statusList, Dish dish) {
        if (quantity > dish.getAvailableQuantity()) {

            List<IngredientQuantity> desiredIngredients = this.dish.getIngredientList();
            List<IngredientQuantity> MissingIngredients = this.dish.getIngredientList();

            desiredIngredients.stream().forEach(ingredientQuantity -> {

                double availableQuantity = ingredientQuantity.getIngredient().getStockQuantityAt().getQuantity();
                double necessaryQuantity = ingredientQuantity.getQuantity() * quantity;
                double missingQuantity = necessaryQuantity - availableQuantity;
                MissingIngredients.add(new IngredientQuantity(ingredientQuantity.getIngredient(), missingQuantity, ingredientQuantity.getUnit()));
            });

            throw new RuntimeException(" not available quantity "+ MissingIngredients);
        }
       else {

            this.dishOrderId = dishOrderId;
            this.quantity = quantity;
            this.statusList = statusList;
            this.dish = dish;
            //this.orderId = orderId;
        }
    }

    public OrderStatus getActualStatus() {
        return statusList.stream().sorted((o1, o2) -> o1.getDishOrderStatusDatetime().compareTo(o2.getDishOrderStatusDatetime())).toList().getLast();
    }
    public OrderStatus updateActualStatus(OrderStatus newStatus) {
        OrderProcess oldStatus = this.getActualStatus().getOrderProcess();
        OrderProcess newOrderStatus = newStatus.getOrderProcess();
        switch (oldStatus) {
            case CREATED -> {
                switch (newOrderStatus) {
                    case CREATED -> {
                        throw new IllegalArgumentException("already created");
                    }case CONFIRMED -> {
                        this.statusList.add(newStatus);
                    }case IN_PREPARATION,FINISHED,DELIVERED ->{
                        throw new IllegalArgumentException("not confirmed yet");
                    }
                }
            }
            case CONFIRMED -> {
                    switch (newOrderStatus) {
                        case CONFIRMED,CREATED -> {
                            throw new IllegalArgumentException("already confirmed");
                        }
                        case IN_PREPARATION -> {
                            this.statusList.add(newStatus);
                        }
                        case FINISHED,DELIVERED -> {
                            throw new IllegalArgumentException("not in preparation yet");
                        }
                    }
            }

        }
return newStatus;

    }




}
