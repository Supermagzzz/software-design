package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.database.Database;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {

    private final Database database;

    public GetProductsServlet(String databaseUrl) {
        this.database = new Database(databaseUrl);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        database.executeQuery("SELECT * FROM PRODUCT", (rs) -> {
            try {
                response.getWriter().println("<html><body>");

                while (rs.next()) {
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    response.getWriter().println(name + "\t" + price + "</br>");
                }
                response.getWriter().println("</body></html>");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
