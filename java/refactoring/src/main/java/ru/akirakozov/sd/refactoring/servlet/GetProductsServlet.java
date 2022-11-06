package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.queries.GetProductsQuery;
import ru.akirakozov.sd.refactoring.utils.HtmlPrinter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class GetProductsServlet extends BaseServlet {

    private final GetProductsQuery getProductsQuery;

    public GetProductsServlet(Database database) {
        this.getProductsQuery = new GetProductsQuery(database);
    }

    @Override
    protected String makeResponse(HttpServletRequest request) {
        return getProductsQuery.processQuery(request);
    }
}
