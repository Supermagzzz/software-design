package ru.akirakozov.sd.refactoring.queries;

import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.utils.HtmlPrinter;

import javax.servlet.http.HttpServletRequest;

public class MinProductsQuery extends Query {
    public MinProductsQuery(Database database) {
        super(database);
    }

    @Override
    public String makeResponse(HttpServletRequest request) {
        HtmlPrinter printer = new HtmlPrinter();
        database.executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1", (rs) -> {
            try {
                printer.println("<h1>Product with min price: </h1>");
                while (rs.next()) {
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    printer.println(name + "\t" + price + "</br>");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return printer.get();
    }
}
