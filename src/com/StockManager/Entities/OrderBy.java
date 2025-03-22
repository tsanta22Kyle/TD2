package com.StockManager.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor@NoArgsConstructor@Getter@Setter
public class OrderBy {
    private OrderValue orderValue;
    private criteriaName orderBy;
}
