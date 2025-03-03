package com.StockManager.DAO;

import java.util.List;

public interface CrudRequests <T>{

    public List<T> findAll(int page,int size);
    public T findById(String id);
    public T save(T entity);
    public List<T> saveAll(List<T> entities);
    public void deleteById(String id);

}
