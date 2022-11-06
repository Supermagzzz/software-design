package ru.akirakozov.sd.refactoring.queries;

import ru.akirakozov.sd.refactoring.data.Product;
import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.utils.HtmlPrinter;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

public class GetProductsQuery extends Query {
    public GetProductsQuery(Database database) {
        super(database);
    }

    @Override
    public String processQuery(HttpServletRequest request) {
        HtmlPrinter printer = new HtmlPrinter();
        for (Product product : database.getAllProducts()) {
            printer.printProduct(product);
        }
        return printer.get();
    }
}
