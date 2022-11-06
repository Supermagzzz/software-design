package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.utils.HtmlPrinter;

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

    public GetProductsServlet(Database database) {
        this.database = database;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        database.executeQuery("SELECT * FROM PRODUCT", (rs) -> {
            try {
                HtmlPrinter htmlPrinter = new HtmlPrinter();

                while (rs.next()) {
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    htmlPrinter.println(name + "\t" + price + "</br>");
                }
                response.getWriter().print(htmlPrinter.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
