package com.StockManager.DAO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
@AllArgsConstructor
public class DataSource {
    private final int port = 5432;
    private String url;
    private final String password = System.getenv("DB_password");
    private final String user = System.getenv("DB_USER");
    private final String database = System.getenv("DB_NAME");
    private final String host = System.getenv("DB_HOST");

    private static Connection connection;

    public DataSource(){
        this.url = "jdbc:postgresql://"+host+":"+port+"/"+database;
    }

    public Connection getConnection(){
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
            }
            return connection;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
