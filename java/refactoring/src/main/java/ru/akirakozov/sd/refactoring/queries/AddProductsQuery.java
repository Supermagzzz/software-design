package ru.akirakozov.sd.refactoring.queries;

import ru.akirakozov.sd.refactoring.data.Product;
import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.utils.HtmlPrinter;

import javax.servlet.http.HttpServletRequest;

public class AddProductsQuery extends Query {
    public AddProductsQuery(Database database) {
        super(database);
    }

    @Override
    public String processQuery(HttpServletRequest request) {
        database.addProduct(new Product(request.getParameter("name"),
                Integer.parseInt(request.getParameter("price"))));
        return "OK";
    }
}