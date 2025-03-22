package com.StockManager.DAO.mapper;

import com.StockManager.Entities.OrderProcess;
import com.StockManager.Entities.OrderStatus;

import java.util.Arrays;
import java.util.List;

public class OrderStatusMapper {
    public OrderProcess mapFormResultSet(String value){
        switch (value){
            case null : return null;
            default: {
                List<OrderProcess> orderStatusList = Arrays.stream(OrderProcess.values()).toList();
                return orderStatusList.stream().filter(orderStatus -> value.equals(orderStatus.toString())).findAny().orElseThrow(()-> new RuntimeException("order not valid "));
            }
        }
    }
}
