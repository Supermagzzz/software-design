package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.queries.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        if (this.queryMap.containsKey(command)) {
            response.getWriter().print(this.queryMap.get(command).makeResponse(request));
        } else {
            response.getWriter().println("Unknown command: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
