package com.StockManager.Entities;

import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor@NoArgsConstructor@Getter@EqualsAndHashCode@Setter
public class OrderStatus {
    private String id;
    private LocalDateTime dishOrderStatusDatetime;
    private OrderProcess orderProcess ;

    public OrderStatus(OrderProcess orderStatus) {
        this.orderProcess = orderStatus;
    }
}
