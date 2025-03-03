package com.StockManager.DAO.mapper;

import com.StockManager.Entities.MoveType;

import java.util.Arrays;
import java.util.List;

public class MoveTypeMapper {
    public MoveType mapFromResultSet(String moveValue){
        if(moveValue == null) return null;
        List<MoveType> moveTypes = Arrays.stream(MoveType.values()).toList();
       return moveTypes.stream().filter(moveType -> moveValue.equals(moveType.toString())).findAny().orElseThrow(()-> new RuntimeException("unvalid moveType"));
    }
}
