package ru.akirakozov.sd.refactoring.database;

import java.sql.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class Database {
    private final String databaseUrl;

    public Database(String databaseUrl) {
        this.databaseUrl = databaseUrl;
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

    public void executeQuery(String sql, Consumer<ResultSet> applyToResult) {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            applyToResult.accept(rs);
            statement.close();
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeUpdate(String sql) {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
