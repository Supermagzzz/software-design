package ru.akirakozov.sd.refactoring.queries;

import ru.akirakozov.sd.refactoring.data.Product;
import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.utils.HtmlPrinter;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

public class MinProductsQuery extends Query {
    public MinProductsQuery(Database database) {
        super(database);
    }

    @Override
    public String processQuery(HttpServletRequest request) {
        HtmlPrinter printer = new HtmlPrinter();
        printer.printHeader("Product with min price: ");
        printer.println();
        Product product = database.getMinProduct();
        if (product != null) {
            printer.printProduct(product);
        }
        return printer.get();
    }
}
