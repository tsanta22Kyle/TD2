package com.StockManager.Entities;


import lombok.*;

@AllArgsConstructor@NoArgsConstructor@EqualsAndHashCode@Getter@Setter
public class Criteria {
    private criteriaName criteriaName;//
    private String operator;
    private Object value;
}
