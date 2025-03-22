package com.StockManager.Tests;

import com.StockManager.DAO.DishOrderCrudRequests;
import com.StockManager.DAO.OrderCrudRequests;
import com.StockManager.Entities.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderIntegrationTests {

    private OrderCrudRequests subject = new OrderCrudRequests();


    @Test
    public void OrderSave() {


        Ingredient saucisse = new Ingredient("1", "saucisse", LocalDateTime.of(2025, 1, 1, 0, 0),
                List.of(new Price("1", 20, LocalDate.of(2025, 3, 1), unit.G), new Price("5", 200, LocalDate.of(2025, 2, 24), unit.G)),
                List.of(new StockMove(MoveType.inComing, 10000, unit.G, LocalDateTime.of(2025, 2, 1, 8, 0))));

        Ingredient huile = new Ingredient("2", "huile", LocalDateTime.of(2025, 1, 1, 0, 0),
                List.of(new Price("2", 10000, LocalDate.of(2025, 3, 1), unit.L), new Price("6", 15000, LocalDate.of(2025, 2, 24), unit.L)),
                List.of(new StockMove(MoveType.inComing, 20, unit.L, LocalDateTime.of(2025, 2, 1, 8, 0))));

        Ingredient oeuf = new Ingredient("3", "oeuf", LocalDateTime.of(2025, 1, 1, 0, 0),
                List.of(new Price("3", 1000, LocalDate.of(2025, 3, 1), unit.U), new Price("7", 2000, LocalDate.of(2025, 2, 24), unit.U)),
                List.of(new StockMove(MoveType.inComing, 100, unit.U, LocalDateTime.of(2025, 2, 1, 8, 0)),
                        new StockMove(MoveType.outComing, 10, unit.U, LocalDateTime.of(2025, 2, 2, 10, 0)),
                        new StockMove(MoveType.outComing, 10, unit.U, LocalDateTime.of(2025, 2, 3, 15, 0))));

        Ingredient pain = new Ingredient("4", "pain", LocalDateTime.of(2025, 1, 1, 0, 0),
                List.of(new Price("4", 1000, LocalDate.of(2025, 3, 1), unit.U), new Price("8", 2000, LocalDate.of(2025, 2, 24), unit.U)),
                List.of(new StockMove(MoveType.inComing, 50, unit.U, LocalDateTime.of(2025, 2, 1, 8, 0)),
                        new StockMove(MoveType.outComing, 20, unit.U, LocalDateTime.of(2025, 2, 5, 16, 0))));

        Dish hotdog = new Dish();
        hotdog.setDishId("1");
        hotdog.setName("hot dog");
        hotdog.setUnitPrice(15000);
        hotdog.addOneIngredient(saucisse, 100, unit.G);
        hotdog.addOneIngredient(huile, 0.15, unit.L);
        hotdog.addOneIngredient(oeuf, 1, unit.U);
        hotdog.addOneIngredient(pain, 1, unit.U);


        DishOrder dishOrder = new DishOrder();
        dishOrder.setDishOrderId("DOR001");
        dishOrder.setQuantity(5);
        dishOrder.setDish(hotdog);


        Order order = new Order();
        order.setId("001");
        order.setOrderDatetime(LocalDateTime.now());
        order.addDishOrder(List.of(dishOrder));
        order.setReference("ORD101");


        // when
        OrderCrudRequests orderCrud = new OrderCrudRequests();
        orderCrud.save(order);

        // then
        Order savedOrder = subject.findById("ORD101");
        Assertions.assertNotNull(savedOrder);
        Assertions.assertEquals("001", savedOrder.getId());
        Assertions.assertEquals(1, savedOrder.getDishOrders().size());
        Assertions.assertEquals("hot dog", savedOrder.getDishOrders().get(0).getDish().getName());


    }
    //Assertions.assertEquals(expected,actual);
}

