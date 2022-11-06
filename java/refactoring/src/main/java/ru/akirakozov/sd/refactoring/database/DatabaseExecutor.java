package ru.akirakozov.sd.refactoring.database;

import ru.akirakozov.sd.refactoring.data.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class DatabaseExecutor {
    private final String databaseUrl;

    public DatabaseExecutor(String databaseUrl) {
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
}
