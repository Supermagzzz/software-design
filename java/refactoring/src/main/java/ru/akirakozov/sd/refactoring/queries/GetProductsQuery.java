package ru.akirakozov.sd.refactoring.queries;

import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.utils.HtmlPrinter;

import javax.servlet.http.HttpServletRequest;

public class GetProductsQuery extends Query {
    public GetProductsQuery(Database database) {
        super(database);
    }

    @Override
    public String processQuery(HttpServletRequest request) {
        HtmlPrinter printer = new HtmlPrinter();
        database.executeQuery("SELECT * FROM PRODUCT", (rs) -> {
            try {
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
