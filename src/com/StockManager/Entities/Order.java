package com.StockManager.Entities;

import lombok.*;

import javax.naming.OperationNotSupportedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Order {
    private String id;
    private LocalDateTime orderDatetime;
    private List<OrderStatus> statusList = new ArrayList<>();
    private List<DishOrder> dishOrderList = new ArrayList<>();
    private String reference;

    public OrderProcess getActualStatus() {
        return statusList.stream().max(Comparator.comparing(orderStatus -> orderStatus.getDishOrderStatusDatetime())).orElse(new OrderStatus(OrderProcess.CREATED)).getOrderProcess();
    }

    public OrderStatus updateStatus(OrderStatus status) {
        OrderProcess oldProcessStatus = this.getActualStatus();
        OrderProcess newStatus = status.getOrderProcess();
        switch (oldProcessStatus) {
            case CREATED -> {
                switch (newStatus) {
                    case CREATED -> {
                        throw new IllegalArgumentException("Order status already created");
                    }
                    case CONFIRMED -> {
                        this.statusList.add(status);
                    }
                    case IN_PREPARATION, FINISHED, DELIVERED ->
                            throw new IllegalArgumentException("order not confirmed yet");
                }

            }
            case CONFIRMED -> {
                switch (newStatus) {
                    case CREATED -> {
                        throw new IllegalArgumentException("Order status already created");
                    }
                    case CONFIRMED -> {
                        throw new IllegalArgumentException("Order status already confirmed");
                    }
                    case IN_PREPARATION -> {
                        this.statusList.add(status);
                    }
                    case FINISHED, DELIVERED -> {
                        throw new IllegalArgumentException("order not prepared yet");
                    }
                }
            }
            case IN_PREPARATION -> {
                switch (newStatus) {
                    case CREATED, CONFIRMED, IN_PREPARATION -> {
                        throw new IllegalArgumentException("Order status already in preparation");
                    }
                    case FINISHED -> {
                        if (!this.getDishOrderList().stream().map(dishOrder -> dishOrder.getStatusList().stream().max(Comparator.comparing(orderStatus -> orderStatus.getDishOrderStatusDatetime()))).toList().contains(OrderProcess.CONFIRMED)) {

                            throw new IllegalArgumentException("Order's dish orders are not confirmed yet ");
                        } else {
                            this.statusList.add(status);
                        }
                    }
                    case DELIVERED -> {
                        throw new IllegalArgumentException("Order status not finished yet");
                    }
                }
            }
            case FINISHED -> {
                switch (newStatus) {
                    case CREATED, CONFIRMED, IN_PREPARATION, FINISHED -> {
                        throw new IllegalArgumentException("Order status already finished");
                    }
                    case DELIVERED -> {
                        if (!this.getDishOrderList().stream().map(dishOrder -> dishOrder.getStatusList().stream().max(Comparator.comparing(orderStatus -> orderStatus.getDishOrderStatusDatetime()))).toList().contains(OrderProcess.FINISHED)) {
                            throw new IllegalArgumentException("Order's dish orders are not finished yet ");
                        } else {
                            this.statusList.add(status);
                        }
                    }
                }
            }
            case DELIVERED -> {
                switch (newStatus) {
                    case CREATED, CONFIRMED, IN_PREPARATION, FINISHED, DELIVERED -> {
                        throw new IllegalArgumentException("Order status already delivered");
                    }
                }
            }
        }
        return status;
    }

    public List<DishOrder> addDishOrder(List<DishOrder> dishOrderList) {
        if (this.getActualStatus() != OrderProcess.CREATED) {
            throw new RuntimeException("this order can't be modified");
        }
        if (getDishOrderList().isEmpty() || getDishOrderList() == null) {
            this.dishOrderList.addAll(dishOrderList);
        }
        this.dishOrderList.addAll(dishOrderList);
        return getDishOrderList();

    }

    public List<DishOrder> getDishOrders() {
        return dishOrderList;
    }

    public double getTotalAmount() {
        if (dishOrderList == null) return 0.0;
        return dishOrderList.stream().mapToDouble(value -> value.getDish().getUnitPrice() * value.getQuantity()).sum();
    }

}
