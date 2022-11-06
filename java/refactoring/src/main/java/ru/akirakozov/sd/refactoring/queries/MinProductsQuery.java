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
        database.executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1", (rs) -> {
            try {
                printer.printHeader("Product with min price: ");
                printer.println();
                while (rs.next()) {
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    printer.printProduct(new Product(name, price));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return printer.get();
    }
}
