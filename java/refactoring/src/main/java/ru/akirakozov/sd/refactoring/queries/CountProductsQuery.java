package ru.akirakozov.sd.refactoring.queries;

import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.utils.HtmlPrinter;

import javax.servlet.http.HttpServletRequest;

public class CountProductsQuery extends Query {
    public CountProductsQuery(Database database) {
        super(database);
    }

    @Override
    public String processQuery(HttpServletRequest request) {
        HtmlPrinter printer = new HtmlPrinter();
        database.executeQuery("SELECT COUNT(*) FROM PRODUCT", (rs) -> {
            try {
                printer.println("Number of products: ");

                if (rs.next()) {
                    printer.println(Integer.toString(rs.getInt(1)));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return printer.get();
    }
}
