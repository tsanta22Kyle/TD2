package com.StockManager.Tests;


import com.StockManager.DAO.DishCrudRequests;
import com.StockManager.Entities.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DishIntegrationTests {
    DishCrudRequests subject = new DishCrudRequests();

    @Test
    public void DishIngredientsTotalCosts() {


        //given
        Dish hotdog = new Dish();
        hotdog.setDishId("1");
        hotdog.setName("hot dog");
        hotdog.setUnitPrice(15000);
        Ingredient saucisse = new Ingredient("1", "saucisse", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("1", 20, LocalDate.of(2025, 3, 1), unit.G), new Price("5", 200, LocalDate.of(2025, 2, 24), unit.G)), List.of(new StockMove(MoveType.inComing,10000,unit.G,LocalDateTime.of(2025,2,1,8,0,0))));
        Ingredient huile = new Ingredient("2", "huile", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("2", 10000, LocalDate.of(2025, 3, 1), unit.L), new Price("6", 15000, LocalDate.of(2025, 2, 24), unit.L)), List.of(new StockMove(MoveType.inComing,20,unit.L,LocalDateTime.of(2025,2,1,8,0,0))));
        Ingredient Oeuf = new Ingredient("3", "oeuf", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("3", 1000, LocalDate.of(2025, 3, 1), unit.U), new Price("7", 2000, LocalDate.of(2025, 2, 24), unit.U)), List.of(new StockMove(MoveType.inComing,100,unit.U,LocalDateTime.of(2025,2,1,8,0,0)),new StockMove(MoveType.outComing,10,unit.U,LocalDateTime.of(2025,2,2,10,0,0)),new StockMove(MoveType.outComing,10,unit.U,LocalDateTime.of(2025,2,3,15,0,0))));
        Ingredient Pain = new Ingredient("4", "pain", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("4", 1000, LocalDate.of(2025, 3, 1), unit.U), new Price("8", 2000, LocalDate.of(2025, 2, 24), unit.U)), List.of(new StockMove(MoveType.inComing,50,unit.U,LocalDateTime.of(2025,2,1,8,0,0)),new StockMove(MoveType.outComing,20,unit.U,LocalDateTime.of(2025,2,5,16,0,0))));
        hotdog.addOneIngredient(saucisse, 100, unit.G);
        hotdog.addOneIngredient(huile, 0.15, unit.L);
        hotdog.addOneIngredient(Oeuf, 1, unit.U);
        hotdog.addOneIngredient(Pain, 1, unit.U);
        double expected1 = hotdog.getIngredientToTalCost();
        double expected2 = hotdog.getIngredientToTalCost(LocalDate.of(2025,02,24));
        //when
        double actual = subject.getTotalCost("1");
        double actual2 = subject.getTotalCost("1",LocalDate.of(2025,02,24));
        //then
        Assertions.assertEquals(expected1, actual);
        Assertions.assertEquals(expected2,actual2);
        // System.out.println(subject.getTotalCost(""));
        System.out.println(expected2+"->"+actual2);
        System.out.println(expected1+"->"+actual);
    }

    @Test
    public void IngredientGrossMargin() {
        //given
        Dish hotdog = new Dish();
        hotdog.setDishId("1");
        hotdog.setName("hot dog");
        hotdog.setUnitPrice(15000);
        Ingredient saucisse = new Ingredient("1", "saucisse", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("1", 20, LocalDate.of(2025, 3, 1), unit.G), new Price("5", 200, LocalDate.of(2025, 2, 24), unit.G)), List.of(new StockMove(MoveType.inComing,10000,unit.G,LocalDateTime.of(2025,2,1,8,0,0))));
        Ingredient huile = new Ingredient("2", "huile", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("2", 10000, LocalDate.of(2025, 3, 1), unit.L), new Price("6", 15000, LocalDate.of(2025, 2, 24), unit.L)), List.of(new StockMove(MoveType.inComing,20,unit.L,LocalDateTime.of(2025,2,1,8,0,0))));
        Ingredient Oeuf = new Ingredient("3", "oeuf", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("3", 1000, LocalDate.of(2025, 3, 1), unit.U), new Price("7", 2000, LocalDate.of(2025, 2, 24), unit.U)), List.of(new StockMove(MoveType.inComing,100,unit.U,LocalDateTime.of(2025,2,1,8,0,0)),new StockMove(MoveType.outComing,10,unit.U,LocalDateTime.of(2025,2,2,10,0,0)),new StockMove(MoveType.outComing,10,unit.U,LocalDateTime.of(2025,2,3,15,0,0))));
        Ingredient Pain = new Ingredient("4", "pain", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("4", 1000, LocalDate.of(2025, 3, 1), unit.U), new Price("8", 2000, LocalDate.of(2025, 2, 24), unit.U)), List.of(new StockMove(MoveType.inComing,50,unit.U,LocalDateTime.of(2025,2,1,8,0,0)),new StockMove(MoveType.outComing,20,unit.U,LocalDateTime.of(2025,2,5,16,0,0))));
        hotdog.addOneIngredient(saucisse, 100, unit.G);
        hotdog.addOneIngredient(huile, 0.15, unit.L);
        hotdog.addOneIngredient(Oeuf, 1, unit.U);
        hotdog.addOneIngredient(Pain, 1, unit.U);

        double actual = subject.GetGrossMargin("1");
        double expected = hotdog.getGrossMargin();
        Assertions.assertEquals(expected, actual);


    }

    @Test
    public void getAvailableQuantity(){
        Dish hotdog = new Dish();
        hotdog.setDishId("1");
        hotdog.setName("hot dog");
        hotdog.setUnitPrice(15000);
        Ingredient saucisse = new Ingredient("1", "saucisse", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("1", 20, LocalDate.of(2025, 3, 1), unit.G), new Price("5", 200, LocalDate.of(2025, 2, 24), unit.G)), List.of(new StockMove(MoveType.inComing,10000,unit.G,LocalDateTime.of(2025,2,1,8,0,0))));
        Ingredient huile = new Ingredient("2", "huile", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("2", 10000, LocalDate.of(2025, 3, 1), unit.L), new Price("6", 15000, LocalDate.of(2025, 2, 24), unit.L)), List.of(new StockMove(MoveType.inComing,20,unit.L,LocalDateTime.of(2025,2,1,8,0,0))));
        Ingredient Oeuf = new Ingredient("3", "oeuf", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("3", 1000, LocalDate.of(2025, 3, 1), unit.U), new Price("7", 2000, LocalDate.of(2025, 2, 24), unit.U)), List.of(new StockMove(MoveType.inComing,100,unit.U,LocalDateTime.of(2025,2,1,8,0,0)),new StockMove(MoveType.outComing,10,unit.U,LocalDateTime.of(2025,2,2,10,0,0)),new StockMove(MoveType.outComing,10,unit.U,LocalDateTime.of(2025,2,3,15,0,0))));
        Ingredient Pain = new Ingredient("4", "pain", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("4", 1000, LocalDate.of(2025, 3, 1), unit.U), new Price("8", 2000, LocalDate.of(2025, 2, 24), unit.U)), List.of(new StockMove(MoveType.inComing,50,unit.U,LocalDateTime.of(2025,2,1,8,0,0)),new StockMove(MoveType.outComing,20,unit.U,LocalDateTime.of(2025,2,5,16,0,0))));
        hotdog.addOneIngredient(saucisse, 100, unit.G);
        hotdog.addOneIngredient(huile, 0.15, unit.L);
        hotdog.addOneIngredient(Oeuf, 1, unit.U);
        hotdog.addOneIngredient(Pain, 1, unit.U);
        StockMove stockMove1 = new StockMove(null,80,unit.U,LocalDateTime.now());
        StockMove stockMove2 = new StockMove(null,30,unit.U,LocalDateTime.now());
        StockMove stockMove3 = new StockMove(null,10000,unit.G,LocalDateTime.now());
        StockMove stockMove4 = new StockMove(null,20,unit.L,LocalDateTime.now());

        double expected = hotdog.getAvailableQuantity(LocalDateTime.now());

        List<StockMove> finalQuantities = new ArrayList<>();
        finalQuantities.add(stockMove1);
        finalQuantities.add(stockMove2);
        finalQuantities.add(stockMove3);
        finalQuantities.add(stockMove4);


        double actual = subject.getAvailableQuantity("1",LocalDateTime.now());

        Assertions.assertEquals(expected,actual);

    }







}
