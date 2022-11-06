package ru.akirakozov.sd.refactoring.database;

import ru.akirakozov.sd.refactoring.data.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Database {
    private final DatabaseExecutor databaseExecutor;

    public Database(String databaseUrl) {
        this.databaseExecutor = new DatabaseExecutor(databaseUrl);
    }

    public void dropIfExists() {
        databaseExecutor.executeUpdate("DROP TABLE IF EXISTS PRODUCT");
    }

    public void createProductDatabase() {
        databaseExecutor.executeUpdate(  "CREATE TABLE IF NOT EXISTS PRODUCT" +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " PRICE          INT     NOT NULL)"
        );
    }

    public void addProduct(Product product) {
        databaseExecutor.executeUpdate("INSERT INTO PRODUCT " +
            "(NAME, PRICE) VALUES (\"" + product.getName() + "\"," + product.getPrice() + ")");
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        databaseExecutor.executeQuery("SELECT * FROM PRODUCT", (rs) -> {
            try {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    products.add(new Product(name, price));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return products;
    }

    private Product selectProduct(String sql) {
        AtomicReference<Product> product = new AtomicReference<>();
        databaseExecutor.executeQuery(sql, (rs) -> {
            try {
                if (rs.next()) {
                    product.set(new Product(rs.getString("name"), rs.getInt("price")));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return product.get();
    }

    private int selectInt(String sql) {
        AtomicInteger result = new AtomicInteger();
        databaseExecutor.executeQuery(sql, (rs) -> {
            try {
                if (rs.next()) {
                    result.set(rs.getInt(1));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return result.get();
    }

    public Product getMaxProduct() {
        return selectProduct("SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1");
    }

    public Product getMinProduct() {
        return selectProduct("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1");
    }

    public Integer getCountProducts() {
        return selectInt("SELECT COUNT(*) FROM PRODUCT");
    }

    public Integer getSumProducts() {
        return selectInt("SELECT SUM(price) FROM PRODUCT");
    }
}
