package ru.akirakozov.sd.refactoring.queries;

import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.utils.HtmlPrinter;

import javax.servlet.http.HttpServletRequest;

public class SumProductsQuery extends Query {
    public SumProductsQuery(Database database) {
        super(database);
    }

    @Override
    public String processQuery(HttpServletRequest request) {
        HtmlPrinter printer = new HtmlPrinter();
        database.executeQuery("SELECT SUM(price) FROM PRODUCT", (rs) -> {
            try {
                printer.println("Summary price: ");
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
