package com.StockManager.DAO;

import com.StockManager.DAO.mapper.OrderStatusMapper;
import com.StockManager.Entities.Dish;
import com.StockManager.Entities.DishOrder;
import com.StockManager.Entities.OrderStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DishOrderCrudRequests implements CrudRequests<DishOrder> {

    private DataSource dataSource;
    private DishCrudRequests dishDAO;
    private OrderStatusMapper orderStatusMapper;

    public DishOrderCrudRequests() {
        this.dataSource = new DataSource();
        this.dishDAO = new DishCrudRequests();
        this.orderStatusMapper = new OrderStatusMapper();
    }

    public DishOrderCrudRequests(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<OrderStatus> getDishOrderStatus(String dishOrderId) {
        List<OrderStatus> orderStatusList = new ArrayList<>();
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT id,dish_order_status.dish_order_id,order_status,dish_order_status.do_datetime from dish_order_status where dish_order_id = ?");
        ) {
            statement.setString(1, dishOrderId);
            try (
                    ResultSet rs = statement.executeQuery();
            ) {
                while (rs.next()) {
                    OrderStatus status = new OrderStatus();
                    status.setDishOrderStatusDatetime(rs.getTimestamp("do_datetime").toLocalDateTime());
                    status.setId(rs.getString("id"));
                    status.setOrderProcess(orderStatusMapper.mapFormResultSet(rs.getObject("order_status").toString()));

                    orderStatusList.add(status);
                }
                return orderStatusList;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public OrderStatus getDishOrderStatusById(String dishOrderStatusId) {

        OrderStatus status = new OrderStatus();
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT id,dish_order_status.dish_order_id,order_status,dish_order_status.do_datetime from dish_order_status where dish_order_id = ?");
        ) {
            statement.setString(1, dishOrderStatusId);
            try (
                    ResultSet rs = statement.executeQuery();
            ) {
                while (rs.next()) {
                    status.setDishOrderStatusDatetime(rs.getTimestamp("do_datetime").toLocalDateTime());
                    status.setId(rs.getString("id"));
                    status.setOrderProcess(orderStatusMapper.mapFormResultSet(rs.getObject("order_status").toString()));

                }
                return status;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DishOrder> findAll(int page, int size) {
        List<DishOrder> dishOrderList = new ArrayList<>();
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT dish_order.id ,order_id,dish_id,quantity from dish_order offset ? limit ? ");
        ) {
            statement.setInt(1, page * (size - 1));
            statement.setInt(2, size);
            try (
                    ResultSet rs = statement.executeQuery()
            ) {
                while (rs.next()) {
                    DishOrder dishOrder = new DishOrder();
                    dishOrder.setDishOrderId(rs.getString("id"));
                  //  dishOrder.setOrderId(rs.getString("order_id"));
                    dishOrder.setStatusList(getDishOrderStatus(rs.getString("id")));
                    dishOrder.setQuantity(rs.getInt("quantity"));
                    dishOrder.setDish(dishDAO.findById(rs.getString("dish_id")));

                    dishOrderList.add(dishOrder);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dishOrderList;
    }


    public List<DishOrder> findByOrder(String orderId) {
        List<DishOrder> dishOrderList = new ArrayList<>();
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT dish_order.id ,order_id,dish_id,quantity from dish_order where order_id = ?");
        ) {
            statement.setString(1, orderId);

            try (
                    ResultSet rs = statement.executeQuery()
            ) {
                while (rs.next()) {
                    DishOrder dishOrder = new DishOrder();
                    dishOrder.setDishOrderId(rs.getString("id"));
                    //  dishOrder.setOrderId(rs.getString("order_id"));
                    dishOrder.setStatusList(getDishOrderStatus(rs.getString("id")));
                    dishOrder.setQuantity(rs.getInt("quantity"));
                    dishOrder.setDish(dishDAO.findById(rs.getString("dish_id")));

                    dishOrderList.add(dishOrder);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dishOrderList;
    }

    @Override
    public DishOrder findById(String dishOrderId) {
        DishOrder dishOrder = new DishOrder();
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT dish_order.id ,order_id,dish_id,quantity from dish_order where id = ?");
        ) {
            statement.setString(1, dishOrderId);
            try (
                    ResultSet rs = statement.executeQuery()
            ) {
                while (rs.next()) {
                    dishOrder.setDishOrderId(rs.getString("id"));
                    //dishOrder.setOrderId(rs.getString("order_id"));
                    dishOrder.setStatusList(getDishOrderStatus(rs.getString("id")));
                    dishOrder.setQuantity(rs.getInt("quantity"));
                    dishOrder.setDish(dishDAO.findById(rs.getString("dish_id")));


                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dishOrder;
    }

    public OrderStatus createDishOrderStatus(OrderStatus dishOrderStatus, String dishOrderId) {
        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("INSERT INTO dish_order_status values (?,?,?)");
        ) {
            statement.setString(1, dishOrderStatus.getId());
            statement.setString(2, dishOrderId);
            statement.setObject(3, dishOrderStatus.getOrderProcess().toString(), Types.OTHER);
            // statement.setTimestamp(4, Timestamp.valueOf(dishOrderStatus.getDishOrderStatusDatetime()));

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return getDishOrderStatusById(dishOrderStatus.getId());
    }



    public Optional<DishOrder> save(DishOrder dishOrder,String orderId) {

        try (
                PreparedStatement statement = dataSource.getConnection().prepareStatement("INSERT INTO dish_order VALUES (?,?,?,?) on conflict (id) do update set quantity = excluded.quantity ,dish_id = excluded.dish_id, order_id = excluded.order_id ")
        ) {
            statement.setString(1, dishOrder.getDishOrderId());
            statement.setString(2, orderId);
            statement.setString(3, dishOrder.getDish().getDishId());
            statement.setInt(4, dishOrder.getQuantity());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(findById(dishOrder.getDishOrderId()));
    }



    public Optional<List<DishOrder>> saveAll(List<DishOrder> dishOrderList) {
        List<DishOrder> dishOrders = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO dish_order (id, order_id, dish_id, quantity)  VALUES (?,?,?,?)"
                        + " on conflict (id) do update set quantity = excluded.quantity ,dish_id = excluded.dish_id, dish_id = excluded.dish_id "
                        + "returning (id, order_id,dish_id,quantity)")
        ) {

            dishOrderList.forEach(DOToSave -> {
                try {
                    statement.setString(1, DOToSave.getDishOrderId());
                   // statement.setString(2, DOToSave.getOrderId());
                    statement.setString(3, DOToSave.getDish().getDishId());
                    statement.setInt(4, DOToSave.getQuantity());
                    statement.addBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            try (
                    ResultSet rs = statement.executeQuery()
            ) {
                while (rs.next()) {
                    dishOrders.add(mapFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return Optional.ofNullable(dishOrders);
    }

    private DishOrder mapFromResultSet(ResultSet rs) {
        DishCrudRequests dishCrudRequests = new DishCrudRequests();
        DishOrder dishOrder = new DishOrder();
        try {
            dishOrder.setQuantity(rs.getInt("quantity"));
            dishOrder.setDishOrderId(rs.getString("id"));
            List<OrderStatus> orderStatusList = getDishOrderStatus(dishOrder.getDishOrderId());
            dishOrder.setStatusList(orderStatusList);
            dishOrder.setDish(dishCrudRequests.findById(rs.getString("dish_id")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return dishOrder;


    }


    public void deleteById(String id) {
        throw new RuntimeException("not implemented yet");

    }
}
