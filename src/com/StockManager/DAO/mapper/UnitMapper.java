package com.StockManager.DAO.mapper;

import com.StockManager.Entities.unit;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
@EqualsAndHashCode
public class UnitMapper {
    public unit mapFromResultSet(String value) {
        if (value == null) return null;
        List<unit> units = Arrays.stream(unit.values()).toList();
        return units.stream().filter(unit -> value.equals(unit.toString())).findAny().orElseThrow(() -> new IllegalArgumentException("invalid unit : " + value));
    }
}
