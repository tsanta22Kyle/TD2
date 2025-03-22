package com.StockManager.DAO;

import java.util.List;
import java.util.Optional;

public interface CrudRequests <T>{

    public List<T> findAll(int page,int size);
    public T findById(String id);

}
