package com.StockManager.DAO;


import com.StockManager.Entities.Dish;
import com.StockManager.Entities.Ingredient;
import com.StockManager.Entities.StockMove;
import net.bytebuddy.implementation.bytecode.Throw;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DishCrudRequests implements CrudRequests<Dish> {

    private DataSource dataSource;
    private IngredientCrudRequests ingredientCrudRequests;

    public DishCrudRequests() {
        this.dataSource = new DataSource();
        this.ingredientCrudRequests = new IngredientCrudRequests();
    }

    @Override
    public List<Dish> findAll(int page, int size) {
        List<Dish> dishes = new ArrayList<>();
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT dish.dish_id,dish.name,dish.unit_price from dish join dish_ingredient di on dish.dish_id = di.dish_id join ingredient i on di.ingredient_id = i.ingredient_id limit ? offset ?")
        ) {
            statement.setInt(1, size);
            statement.setInt(2, size * (page - 1));
            try (
                    ResultSet rs = statement.executeQuery()
            ) {
                Dish dish = new Dish();
                while (rs.next()) {
                    dish.setDishId(rs.getString("dish_id"));
                    dish.setName(rs.getString(2));
                    dish.setUnitPrice(rs.getInt("unit_price"));
                    dish.setIngredientList(ingredientCrudRequests.findIngredientByDishId(dish.getDishId()));

                }
                dishes.add(dish);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return dishes;
    }

    @Override
    public Dish findById(String dishId) {
        Dish dish = new Dish();
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT dish.dish_id,dish.name,dish.unit_price from dish join dish_ingredient di on dish.dish_id = di.dish_id join ingredient i on di.ingredient_id = i.ingredient_id where di.dish_id = ?")
        ) {
            statement.setString(1,dishId);
            try (
                    ResultSet rs = statement.executeQuery()
            ) {

                while (rs.next()) {
                    dish.setDishId(rs.getString("dish_id"));
                    dish.setName(rs.getString(2));
                    dish.setUnitPrice(rs.getInt("unit_price"));
                    dish.setIngredientList(ingredientCrudRequests.findIngredientByDishId(dishId));

                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return dish;
    }

    @Override
    public Dish save(Dish dish) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public List<Dish> saveAll(List<Dish> entities) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void deleteById(String id) {
        throw new RuntimeException("not implemented yet");
    }

    public double getTotalCost(String dishId, LocalDate date) {
        double TotalCost = 0.0;

        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT SUM(required_quantity*value) from dish d join dish_ingredient di  on d.dish_id = di.dish_id join ingredient i on i.ingredient_id = di.ingredient_id join price on price.ingredient_id = i.ingredient_id WHERE date = ? AND d.dish_id=?")
        ) {
            statement.setDate(1, Date.valueOf(date));
            statement.setString(2, dishId);
            try (
                    ResultSet rs = statement.executeQuery();
            ) {
                while (rs.next()) {
                    TotalCost = rs.getDouble(1);
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return TotalCost;

    }

    public double getTotalCost(String dishId) {
        double TotalCost = 0.0;

        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("select sum(value*required_quantity) latest_cost  from dish d join dish_ingredient di on d.dish_id=di.dish_id join ingredient i on i.ingredient_id=di.ingredient_id join price p on p.ingredient_id = i.ingredient_id join latests l on l.ingredient_id = p.ingredient_id where p.date = l.latest AND d.dish_id=?")
        ) {
            statement.setString(1, dishId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    TotalCost = rs.getDouble(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return TotalCost;

    }


    public double GetGrossMargin(LocalDate date, String dishId) {
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("select dish.unit_price from dish where dish_id = ?")
                //PreparedStatement getIngredientTotalPriceByDate = dataSource.getConnection().prepareStatement("SELECT SUM(value) from dish d join dish_ingredient di  on d.dish_id = di.dish_id join ingredient i on i.ingredient_id = di.ingredient_id join price on price.ingredient_id = i.ingredient_id WHERE date = ? AND d.dish_id=?;");
        ) {
            statement.setString(1, dishId);
            double unitPrice = 0.0;
            double totalIngredientCosts = getTotalCost(dishId, date);
            try (
                    ResultSet rs = statement.executeQuery();
            ) {
                while (rs.next()) {
                    unitPrice = rs.getDouble(1);
                }


            }
            return unitPrice - totalIngredientCosts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public double GetGrossMargin(String dishId) {

        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("select dish.unit_price from dish where dish_id = ?");
                //PreparedStatement getIngredientTotalPriceByDate = dataSource.getConnection().prepareStatement("SELECT SUM(value) from dish d join dish_ingredient di  on d.dish_id = di.dish_id join ingredient i on i.ingredient_id = di.ingredient_id join price on price.ingredient_id = i.ingredient_id WHERE date = ? AND d.dish_id=?;");
        ) {
            statement.setString(1, dishId);
            double unitPrice = 0.0;
            double totalIngredientCosts = getTotalCost(dishId);
            try (
                    ResultSet rs = statement.executeQuery();
            ) {
                while (rs.next()) {
                    unitPrice = rs.getDouble(1);
                }


            }
            return unitPrice - totalIngredientCosts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //public List<S> get

    public double getAvailableQuantity(String dishId, LocalDateTime dateTime) {
        List<StockMove> IngredientsFinalQuantities = ingredientCrudRequests.getTotalStockQuantityInDish(dishId, dateTime);
        Dish thisDish = findById(dishId);
        List<Double> quantities = new ArrayList<>();
      //  double quantity = 0.0;

            IngredientsFinalQuantities.forEach(stockMove -> {
                int index = IngredientsFinalQuantities.indexOf(stockMove);
           double quantity = thisDish.getIngredientList().get(index).getQuantity();

            quantities.add(stockMove.getQuantity()/quantity);
            });



     return Math.round(quantities.stream().sorted((o1, o2) -> o1.compareTo(o2)).toList().getFirst());
    }

    public static void main(String[] args) {
        DishCrudRequests d = new DishCrudRequests();
        System.out.println(d.getAvailableQuantity("1",LocalDateTime.now()));
    }

}
