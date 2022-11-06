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
public class QueryServlet extends HttpServlet {

    private final Database database;

    public QueryServlet(Database database) {
        this.database = database;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        if ("max".equals(command)) {
            database.executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1", (rs) -> {
                try {
                    HtmlPrinter printer = new HtmlPrinter();
                    printer.println("<h1>Product with max price: </h1>");

                    while (rs.next()) {
                        String name = rs.getString("name");
                        int price = rs.getInt("price");
                        printer.println(name + "\t" + price + "</br>");
                    }
                    response.getWriter().print(printer.get());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } else if ("min".equals(command)) {
            database.executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1", (rs) -> {
                try {
                    HtmlPrinter printer = new HtmlPrinter();
                    printer.println("<h1>Product with min price: </h1>");

                    while (rs.next()) {
                        String name = rs.getString("name");
                        int price = rs.getInt("price");
                        printer.println(name + "\t" + price + "</br>");
                    }
                    response.getWriter().print(printer.get());
                } catch (Exception e) {
                    throw new RuntimeException(e);

                }
            });
        } else if ("sum".equals(command)) {
            database.executeQuery("SELECT SUM(price) FROM PRODUCT", (rs) -> {
                try {
                    HtmlPrinter printer = new HtmlPrinter();
                    printer.println("Summary price: ");

                    if (rs.next()) {
                        printer.println(Integer.toString(rs.getInt(1)));
                    }
                    response.getWriter().print(printer.get());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } else if ("count".equals(command)) {
            database.executeQuery("SELECT COUNT(*) FROM PRODUCT", (rs) -> {
                try {
                    HtmlPrinter printer = new HtmlPrinter();
                    printer.println("Number of products: ");

                    if (rs.next()) {
                        printer.println(Integer.toString(rs.getInt(1)));
                    }
                    response.getWriter().print(printer.get());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            response.getWriter().println("Unknown command: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
