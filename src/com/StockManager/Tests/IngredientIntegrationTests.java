package com.StockManager.Tests;

import com.StockManager.DAO.IngredientCrudRequests;
import com.StockManager.Entities.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class IngredientIntegrationTests {

    IngredientCrudRequests subject = new IngredientCrudRequests();

    @Test
    public void getStockMove() {
        //GIVEN
        Dish hotdog = new Dish();
        hotdog.setDishId("1");
        hotdog.setName("hot dog");
        hotdog.setUnitPrice(15000);
        Ingredient saucisse = new Ingredient("1", "saucisse", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("1", 20, LocalDate.of(2025, 3, 1), unit.G), new Price("5", 200, LocalDate.of(2025, 2, 24), unit.G)), List.of(new StockMove(MoveType.inComing, 10000, unit.G, LocalDateTime.of(2025, 2, 1, 8, 0, 0))));
        Ingredient huile = new Ingredient("2", "huile", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("2", 10000, LocalDate.of(2025, 3, 1), unit.L), new Price("6", 15000, LocalDate.of(2025, 2, 24), unit.L)), List.of(new StockMove(MoveType.inComing, 20, unit.L, LocalDateTime.of(2025, 2, 1, 8, 0, 0))));
        Ingredient Oeuf = new Ingredient("3", "oeuf", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("3", 1000, LocalDate.of(2025, 3, 1), unit.U), new Price("7", 2000, LocalDate.of(2025, 2, 24), unit.U)), List.of(new StockMove(MoveType.inComing, 100, unit.U, LocalDateTime.of(2025, 2, 1, 8, 0, 0)), new StockMove(MoveType.outComing, 10, unit.U, LocalDateTime.of(2025, 2, 2, 10, 0, 0)), new StockMove(MoveType.outComing, 10, unit.U, LocalDateTime.of(2025, 2, 3, 15, 0, 0))));
        Ingredient Pain = new Ingredient("4", "pain", LocalDateTime.of(2025, 1, 1, 0, 0), List.of(new Price("4", 1000, LocalDate.of(2025, 3, 1), unit.U), new Price("8", 2000, LocalDate.of(2025, 2, 24), unit.U)), List.of(new StockMove(MoveType.inComing, 50, unit.U, LocalDateTime.of(2025, 2, 1, 8, 0, 0)), new StockMove(MoveType.outComing, 20, unit.U, LocalDateTime.of(2025, 2, 5, 16, 0, 0))));
        hotdog.addOneIngredient(saucisse, 100, unit.G);
        hotdog.addOneIngredient(huile, 0.15, unit.L);
        hotdog.addOneIngredient(Oeuf, 1, unit.U);
        hotdog.addOneIngredient(Pain, 1, unit.U);
        //WHEN
        List<StockMove> expected = List.of(new StockMove(MoveType.inComing, 10000, unit.G, LocalDateTime.of(2025, 2, 1, 8, 0, 0)), new StockMove(MoveType.inComing, 20, unit.L, LocalDateTime.of(2025, 2, 1, 8, 0, 0)), new StockMove(MoveType.inComing, 100, unit.U, LocalDateTime.of(2025, 2, 1, 8, 0, 0)), new StockMove(MoveType.inComing, 50, unit.U, LocalDateTime.of(2025, 2, 1, 8, 0, 0)));
        List<StockMove> actual = subject.getAllStock(MoveType.inComing);
        //THEN

        Assertions.assertEquals(expected, actual);
        System.out.println(expected);
        System.out.println(actual);
    }

    @Test
    public void createMove() {
        //GIVEN
        Ingredient salt = new Ingredient();
        salt.setIngredientId("5");
        salt.setPrices(List.of(new Price("10",2.5,LocalDate.of(2025,03,1),unit.G)));
        salt.setName("sel");
        salt.setUpdateDatetime(LocalDateTime.now());

        Ingredient rice = new Ingredient();
        rice.setIngredientId("6");
        rice.setPrices(List.of(new Price("10",3.5,LocalDate.of(2025,03,1),unit.G)));
        salt.setName("riz");
        salt.setUpdateDatetime(LocalDateTime.now());


        StockMove comingSalt = new StockMove();
        comingSalt.setUnit(unit.G);
        comingSalt.setQuantity(100);
        comingSalt.setMoveDate(LocalDateTime.now());
        comingSalt.setMoveType(MoveType.inComing);

        StockMove comingRice = new StockMove();
        comingRice.setUnit(unit.G);
        comingRice.setQuantity(1000);
        comingRice.setMoveDate(LocalDateTime.now());
        comingRice.setMoveType(MoveType.inComing);
         StockMove outSalt = new StockMove();
        outSalt.setUnit(unit.G);
        outSalt.setQuantity(10);
        outSalt.setMoveDate(LocalDateTime.now());
        outSalt.setMoveType(MoveType.outComing);
        StockMove outRice = new StockMove();
        outRice.setUnit(unit.G);
        outRice.setQuantity(500);
        outRice.setMoveDate(LocalDateTime.now());
        outRice.setMoveType(MoveType.outComing);
        List<StockMove> expectedSaltStockmoves = List.of(outSalt,comingSalt);
        List<StockMove> expectedRiceStockmoves = List.of(outRice,comingRice);


                //WHEN
        subject.createStock("5",comingSalt);
        subject.createStock("6",comingRice);
        subject.createStock("5",outRice);
        subject.createStock("6",outSalt);

       List<StockMove> saltStockmoves = subject.getAllStockMovesByIngredientId("5");
       List<StockMove> riceStockmoves = subject.getAllStockMovesByIngredientId("6");

       // System.out.println(saltStockmoves);
        System.out.println(riceStockmoves);
        //System.out.println("expected : "+expectedSaltStockmoves);
        System.out.println("expected : "+expectedSaltStockmoves);
      //  Assertions.assertEquals(expectedSaltStockmoves,saltStockmoves);
        //Assertions.assertEquals(expectedRiceStockmoves,riceStockmoves);



    }
}
