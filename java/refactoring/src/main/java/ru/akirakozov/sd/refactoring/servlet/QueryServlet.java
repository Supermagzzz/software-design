package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.queries.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class QueryServlet extends BaseServlet {

    private final Map<String, Query> queryMap;

    public QueryServlet(Database database) {
        this.queryMap = Map.of(
            "max", new MaxProductsQuery(database),
            "min", new MinProductsQuery(database),
            "sum", new SumProductsQuery(database),
            "count", new CountProductsQuery(database)
        );
    }

    @Override
    protected String makeResponse(HttpServletRequest request) {
        String command = request.getParameter("command");

        if (this.queryMap.containsKey(command)) {
            return this.queryMap.get(command).processQuery(request);
        } else {
            return "Unknown command: " + command;
        }
    }
}
