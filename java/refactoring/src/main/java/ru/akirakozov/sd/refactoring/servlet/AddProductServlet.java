package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.queries.AddProductsQuery;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class AddProductServlet extends BaseServlet {

    private final AddProductsQuery addProductsQuery;

    public AddProductServlet(Database database) {
        this.addProductsQuery = new AddProductsQuery(database);
    }

    @Override
    protected String makeResponse(HttpServletRequest request) {
        return addProductsQuery.processQuery(request);
    }
}
