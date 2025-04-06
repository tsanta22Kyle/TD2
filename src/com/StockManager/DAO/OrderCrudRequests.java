package com.StockManager.DAO;

import com.StockManager.DAO.mapper.OrderStatusMapper;
import com.StockManager.Entities.DishOrder;
import com.StockManager.Entities.Order;
import com.StockManager.Entities.OrderProcess;
import com.StockManager.Entities.OrderStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class OrderCrudRequests implements CrudRequests<Order> {

    private DataSource dataSource;
    private DishOrderCrudRequests dishOrderCrudRequests;
    private OrderStatusMapper mapper;

    public OrderCrudRequests() {
        this.dataSource = new DataSource();
        this.dishOrderCrudRequests = new DishOrderCrudRequests();
        this.mapper = new OrderStatusMapper();
    }

    @Override
    public List<Order> findAll(int page, int size) {
        List<Order> orders = new ArrayList<Order>();
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM order ORDER BY order_id ASC limit ? offset ?");
        ) {
            ps.setInt(1, size);
            ps.setInt(2, page * (size - 1));
            try (
                    ResultSet rs = ps.executeQuery();
            ) {
                orders.add(MapFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orders;
    }

    @Override
    public Order findById(String reference) {
        Order order = null;
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT order_id,reference,order_time FROM \"order\" WHERE reference = ?");
        ) {
            ps.setString(1, reference);
            try (ResultSet rs = ps.executeQuery()) {
                order = MapFromResultSet(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return order;
    }

    public OrderStatus createStatus(OrderStatus status, String orderID) {
        OrderStatus status1 = new OrderStatus();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO order_status (order_id, id, order_status_datetime, order_status) " +
                             "VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING RETURNING order_id, id, order_status_datetime, order_status"
             )) {

            ps.setString(1, orderID);
            ps.setString(2, status.getId());
            ps.setTimestamp(3, Timestamp.valueOf(status.getDishOrderStatusDatetime()));
            ps.setObject(4, status.getOrderProcess().toString(), Types.OTHER);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    status1.setId(rs.getString("id"));
                    status1.setDishOrderStatusDatetime(rs.getTimestamp("order_status_datetime").toLocalDateTime());
                    status1.setOrderProcess(mapper.mapFormResultSet(rs.getObject("order_status").toString()));
                } else {
                    throw new RuntimeException("can't create status for " + orderID);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return status1;
    }


    public Optional<Order> save(Order order) {
        Order order1 = null;
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO \"order\" (reference, order_time, order_id) VALUES (?,?,?) " +
                        "on conflict(reference) do update  SET order_time = excluded.order_time " +
                        "returning reference, order_time, order_id")
        ) {
            ps.setString(1, order.getReference());
            ps.setTimestamp(2, Timestamp.valueOf(order.getOrderDatetime()));
            ps.setString(3, order.getId());
            OrderStatus statusCreated = new OrderStatus();
            statusCreated.setOrderProcess(OrderProcess.CREATED);
            statusCreated.setId(UUID.randomUUID().toString());
            statusCreated.setDishOrderStatusDatetime(LocalDateTime.now());
            try (ResultSet rs = ps.executeQuery()) {
                order1 = (MapFromResultSet(rs));
                createStatus(statusCreated, order.getId());
                order.getDishOrderList().forEach(dishOrder -> {
                    dishOrderCrudRequests.save(dishOrder, order.getId());
                });
            }

            return Optional.of(order1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public Optional<List<Order>> saveAll(List<Order> orders) {
        List<Order> ordersCreated = new ArrayList<>();
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO \"order\" (reference, order_time, order_id) VALUES (?,?,?) " +
                        "on conflict (reference) do update set order_time = excluded.order_time " +
                        "returning reference, order_time, order_id")
        ) {
            orders.forEach(order -> {
                try {
                    ps.setString(1, order.getReference());
                    ps.setTimestamp(2, Timestamp.valueOf(order.getOrderDatetime()));
                    ps.setString(3, order.getId());
                    OrderStatus statusCreated = new OrderStatus();
                    for (DishOrder dishOrder : order.getDishOrderList()) {
                    dishOrderCrudRequests.save(dishOrder, order.getId());
                    }

                    try (
                            ResultSet rs = ps.executeQuery()
                    ) {
                        if (rs.next()) {
                            statusCreated.setOrderProcess(OrderProcess.CREATED);
                            statusCreated.setId(UUID.randomUUID().toString());
                            statusCreated.setDishOrderStatusDatetime(LocalDateTime.now());
                            createStatus(statusCreated, order.getId());


                            ordersCreated.add(MapFromResultSet(rs));
                        }
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(ordersCreated);
    }

    public Order MapFromResultSet(ResultSet rs) {
        Order order = new Order();
        try {

            if (rs.next()) {
                order.setId(rs.getString("order_id"));
                order.setOrderDatetime(rs.getTimestamp("order_time").toLocalDateTime());
                order.setDishOrderList(dishOrderCrudRequests.findByOrder(order.getId()));
                order.setReference(rs.getString("reference"));
                order.setStatusList(dishOrderCrudRequests.getDishOrderStatus(order.getId()));
                order.setDishOrderList(dishOrderCrudRequests.findByOrder(order.getId()));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return order;
    }

}
