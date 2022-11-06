package ru.akirakozov.sd.refactoring.database;

import ru.akirakozov.sd.refactoring.data.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Database {
    private final String databaseUrl;

    public Database(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public void executeQuery(String sql, Consumer<ResultSet> applyToResult) {
        try (
            Connection connection = DriverManager.getConnection(databaseUrl);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql)
        ) {
            applyToResult.accept(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeUpdate(String sql) {
        try (
            Connection connection = DriverManager.getConnection(databaseUrl);
            Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropIfExists() {
        executeUpdate("DROP TABLE IF EXISTS PRODUCT");
    }

    public void createProductDatabase() {
        executeUpdate(  "CREATE TABLE IF NOT EXISTS PRODUCT" +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " PRICE          INT     NOT NULL)"
        );
    }

    public void addProduct(Product product) {
        executeUpdate("INSERT INTO PRODUCT " +
            "(NAME, PRICE) VALUES (\"" + product.getName() + "\"," + product.getPrice() + ")");
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        executeQuery("SELECT * FROM PRODUCT", (rs) -> {
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

    public Product getMaxProduct() {
        AtomicReference<Product> maxProduct = new AtomicReference<>();
        executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1", (rs) -> {
            try {
                while (rs.next()) {
                    maxProduct.set(new Product(rs.getString("name"), rs.getInt("price")));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return maxProduct.get();
    }

    public Product getMinProduct() {
        AtomicReference<Product> minProduct = new AtomicReference<>();
        executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1", (rs) -> {
            try {
                while (rs.next()) {
                    minProduct.set(new Product(rs.getString("name"), rs.getInt("price")));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return minProduct.get();
    }

    public Integer getCountProducts() {
        AtomicInteger result = new AtomicInteger();
        executeQuery("SELECT COUNT(*) FROM PRODUCT", (rs) -> {
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

    public Integer getSumProducts() {
        AtomicInteger result = new AtomicInteger();
        executeQuery("SELECT SUM(price) FROM PRODUCT", (rs) -> {
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
}
