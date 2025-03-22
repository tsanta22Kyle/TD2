package com.StockManager.DAO;

import com.StockManager.DAO.mapper.MoveTypeMapper;
import com.StockManager.DAO.mapper.UnitMapper;
import com.StockManager.Entities.*;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class IngredientCrudRequests implements CrudRequests {

    private DataSource dataSource;
    private UnitMapper unitMapper;
    private MoveTypeMapper moveTypeMapper;

    public IngredientCrudRequests() {
        this.dataSource = new DataSource();
        this.unitMapper = new UnitMapper();
        this.moveTypeMapper = new MoveTypeMapper();
    }

    @Override
    public List findAll(int page, int size) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public Object findById(String id) {

        throw new RuntimeException("not implemented yet");
    }

    public List<Price> findPriceByIngredientId(String id) {
        List<Price> prices = new ArrayList<>();
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT price.price_id,value,date,unit from price where ingredient_id = ? ORDER BY date ASC");
        ) {
            statement.setString(1, id);
            try (
                    ResultSet rs = statement.executeQuery()
            ) {
                Price price = new Price();
                while (rs.next()) {
                    //price.setDate(LocalDate.of(rs.getDate("date").getYear(),rs.getDate("date").getMonth(),rs.getDate("date").getDay()));
                    price.setDate(rs.getDate("date").toLocalDate());
                    price.setId(rs.getString("price_id"));
                    price.setUnit(unitMapper.mapFromResultSet(rs.getObject("unit").toString()));
                    price.setValue(rs.getInt("value"));

                    prices.add(price);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return prices;
    }

    public List<IngredientQuantity> findIngredientByDishId(String DishId) {
        List<IngredientQuantity> ingredientQuantities = new ArrayList<>();
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT dish.dish_id,dish.unit_price,i.ingredient_id,i.name,update_datetime,required_quantity,unit from dish join dish_ingredient di on dish.dish_id = di.dish_id join ingredient i on di.ingredient_id = i.ingredient_id WHERE di.dish_id = ? ORDER BY ingredient_id ASC")
        ) {
            statement.setString(1, DishId);
            try (
                    ResultSet rs = statement.executeQuery()
            ) {
                while (rs.next()) {

                    Ingredient ingredient = new Ingredient();
                    //  LocalDateTime updateDateTime = LocalDateTime.of(rs.getTimestamp("update_datetime").getYear(),rs.getTimestamp("update_datetime").getDate(),rs.getTimestamp("update_datetime").getDay(),rs.getTimestamp("update_datetime").getHours(),rs.getTimestamp("update_datetime").getMinutes());
                    //LocalDateTime up = rs.getTimestamp().toLocalDateTime()
                    ingredient.setIngredientId(rs.getString("ingredient_id"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setPrices(findPriceByIngredientId(ingredient.getIngredientId()));
                    ingredient.setUpdateDatetime(rs.getTimestamp("update_datetime").toLocalDateTime());
                    ingredientQuantities.add(new IngredientQuantity(ingredient, rs.getDouble("required_quantity"), unitMapper.mapFromResultSet(rs.getObject("unit").toString())));
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ingredientQuantities;
    }


    public Optional<Object> save(Object entity) {
        throw new RuntimeException("not implemented yet");
    }


    public Optional<List> saveAll(List entities) {
        throw new RuntimeException("not implemented yet");
    }


    public void deleteById(String id) {
        throw new RuntimeException("not implemented yet");

    }

    public List<Ingredient> findByFilterAndOrder(List<Criteria> criteriaList, OrderBy order, int page, int size) {
        List<Ingredient> ingredients = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT i.ingredient_id,i.name,i.update_datetime from ingredient i  join price p on p.ingredient_id = i.ingredient_id join latests l on l.ingredient_id = p.ingredient_id where p.date = l.latest and 1=1");
        criteriaList.stream().forEach(criteria -> {
            String column = criteria.getCriteriaName().toString();
            String operator = criteria.getOperator();
            //  Object value = criteria.getValue();
            query.append(" ").append("AND ").append(column).append(" ").append(operator).append(" ").append('?').append(" ");
            //;;;;; AND name ILIKE ? AND date = ? AND
        });
        query.append("ORDER BY ").append(order.getOrderBy().toString()).append(" ").append(order.getOrderValue().toString());
        query.append(" limit ?").append(" offset ?");
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement(query.toString())
        ) {
            criteriaList.stream().forEach(criteria -> {
                int index = criteriaList.indexOf(criteria);
                String operator = criteria.getOperator();
                try {
                    statement.setInt(criteriaList.size() + 1, size);
                    statement.setInt(criteriaList.size() + 2, size * (page - 1));

                    if (operator.equals("LIKE") || operator.equals("ILIKE")) {
                        statement.setString(index + 1, "%" + criteria.getValue().toString() + "%");
                    } else {
                        statement.setObject(index + 1, criteria.getValue(), Types.OTHER);
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setIngredientId(rs.getString(1));
                    ingredient.setName(rs.getString(2));
                    ingredient.setUpdateDatetime(rs.getTimestamp(3).toLocalDateTime());
                    ingredient.setPrices(findPriceByIngredientId(ingredient.getIngredientId()));
                    ingredients.add(ingredient);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return ingredients;
    }


    public List<StockMove> getAllStock() {
        List<StockMove> stockMoves = new ArrayList<>();
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT movetype , unit,quantity , move_date FROM stock_move ")
        ) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    StockMove stockMove = new StockMove(moveTypeMapper.mapFromResultSet(rs.getObject("movetype").toString()), rs.getDouble("quantity"), unitMapper.mapFromResultSet(rs.getObject("unit").toString()), rs.getTimestamp("move_date").toLocalDateTime());
                    stockMoves.add(stockMove);
                }
            }
            return stockMoves;

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }

    }

    public List<StockMove> getAllStock(MoveType moveType) {
        List<StockMove> stockMoves = new ArrayList<>();
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT movetype , unit,quantity , move_date FROM stock_move  where movetype=?")
        ) {
            statement.setObject(1, moveType.toString(), Types.OTHER);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    StockMove stockMove = new StockMove(moveTypeMapper.mapFromResultSet(rs.getObject("movetype").toString()), rs.getDouble("quantity"), unitMapper.mapFromResultSet(rs.getObject("unit").toString()), rs.getTimestamp("move_date").toLocalDateTime());
                    stockMoves.add(stockMove);
                }
            }
            return stockMoves;

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }

    }

    public List<StockMove> getAllStockMovesByIngredientId(String ingredientId) {
        List<StockMove> stockMoves = new ArrayList<>();
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT movetype , unit,quantity , move_date FROM stock_move WHERE ingredient_id = ?")
        ) {
            statement.setString(1, ingredientId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    StockMove stockMove = new StockMove(moveTypeMapper.mapFromResultSet(rs.getObject("movetype").toString()), rs.getDouble("quantity"), unitMapper.mapFromResultSet(rs.getObject("unit").toString()), rs.getTimestamp("move_date").toLocalDateTime());
                    stockMoves.add(stockMove);
                }
            }
            return stockMoves;

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }


    }

    public List<StockMove> getAllStockMovesByIngredientId(String ingredientId, MoveType moveType, LocalDateTime moveDate) {
        List<StockMove> stockMoves = new ArrayList<>();
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT movetype , unit,quantity , move_date FROM stock_move WHERE ingredient_id = ? AND movetype = ? AND move_date <= ? ORDER BY ingredient_id ASC")
        ) {
            statement.setString(1, ingredientId);
            statement.setObject(2, moveType.toString(), Types.OTHER);
            statement.setTimestamp(3, Timestamp.valueOf(moveDate));
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    StockMove stockMove = new StockMove(moveTypeMapper.mapFromResultSet(rs.getObject("movetype").toString()), rs.getDouble("quantity"), unitMapper.mapFromResultSet(rs.getObject("unit").toString()), rs.getTimestamp("move_date").toLocalDateTime());
                    stockMoves.add(stockMove);
                }
            }
            return stockMoves;

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }


    }

    public List<StockMove> getAllStockMovesByIngredientId(String ingredientId, LocalDateTime moveDate) {
        List<StockMove> stockMoves = new ArrayList<>();
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT movetype , unit,quantity , move_date FROM stock_move WHERE ingredient_id = ? and move_date<=?")
        ) {
            statement.setString(1, ingredientId);
            statement.setTimestamp(2, Timestamp.valueOf(moveDate));
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    StockMove stockMove = new StockMove(moveTypeMapper.mapFromResultSet(rs.getObject("movetype").toString()), rs.getDouble("quantity"), unitMapper.mapFromResultSet(rs.getObject("unit").toString()), rs.getTimestamp("move_date").toLocalDateTime());
                    stockMoves.add(stockMove);
                }
            }
            return stockMoves;

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }


    }

    public List<StockMove> getTotalStockQuantityInDish(String dishId, LocalDateTime date) {
        List<IngredientQuantity> requiredIngredients = findIngredientByDishId(dishId);
        List<StockMove> finalQuantityStockMoves = new ArrayList<>();
        requiredIngredients.stream().forEach(ingredientQuantity -> {
            String ingredientId = ingredientQuantity.getIngredient().getIngredientId();
            List<StockMove> incomingStockMoves = getAllStockMovesByIngredientId(ingredientId, MoveType.inComing, date);
            List<StockMove> outComingStockMoves = getAllStockMovesByIngredientId(ingredientId, MoveType.outComing, date);
            double incomingStockIngredient = incomingStockMoves.stream().mapToDouble(stockmove -> stockmove.getQuantity()).sum();
            double outcomingStockIngredient = outComingStockMoves.stream().mapToDouble(stockmove -> stockmove.getQuantity()).sum();
            double totalStockIngredient = incomingStockIngredient - outcomingStockIngredient;
            StockMove stockQuantity = new StockMove();
            stockQuantity.setQuantity(totalStockIngredient);
            stockQuantity.setMoveDate(date);
            stockQuantity.setUnit(ingredientQuantity.getUnit());
            finalQuantityStockMoves.add(stockQuantity);
        });
        return finalQuantityStockMoves;
    }

    ;

    public void createStock(String ingredientId, StockMove stockMove) {
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement("INSERT INTO stock_move VALUES(?,?,?,?,?)")) {
            statement.setString(1, ingredientId);
            statement.setObject(2, stockMove.getMoveType().toString(), Types.OTHER);
            statement.setDouble(3, stockMove.getQuantity());
            statement.setObject(4, stockMove.getUnit().toString(), Types.OTHER);
            statement.setTimestamp(5, Timestamp.valueOf(stockMove.getMoveDate()));

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //public List<StockMove> getAllS

    public static void main(String[] args) {
        IngredientCrudRequests i = new IngredientCrudRequests();
        // System.out.println(i.findByFilterAndOrder(List.of(new Criteria(criteriaName.name,"ILIKE","e"),new Criteria(criteriaName.value,">",0),new Criteria(criteriaName.unit,"=",unit.L)),new Order(OrderValue.ASC,criteriaName.name),1,5));
        System.out.println(i.getTotalStockQuantityInDish("1", LocalDateTime.now()));
    }


}
