package ru.akirakozov.sd.refactoring.servlet;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import ru.akirakozov.sd.refactoring.data.Product;
import ru.akirakozov.sd.refactoring.database.Database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class Tests {
    private final static String EXPECTED_START = "<html><body>";
    private final static String EXPECTED_END = "</body></html>";
    private static final Integer RANDOM_TESTS_COUNT = 20;
    private static final Integer MAX_RANDOM_PRODUCTS_COUNT = 20;
    private static final Integer MAX_RANDOM_STRING_LENGTH = 10;
    private static final Integer MAX_RANDOM_PRICE = 10;
    private static final Random random = new Random();

    private static String databaseUrl;
    private static Database database;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private static String randomString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append('a' + (char)random.nextInt(26));
        }
        return builder.toString();
    }

    @BeforeClass
    public static void createDatabaseDirectory() throws IOException {
        Path databaseDirectory = Files.createTempDirectory(randomString(MAX_RANDOM_STRING_LENGTH));
        databaseDirectory.toFile().deleteOnExit();
        databaseUrl = "jdbc:sqlite:" + databaseDirectory.resolve("test.db");
        database = new Database(databaseUrl);
    }

    @Before
    public void createDatabase() {
        database.executeUpdate("DROP TABLE IF EXISTS PRODUCT");
        database.executeUpdate(  "CREATE TABLE PRODUCT" +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " PRICE          INT     NOT NULL)"
        );
    }

    private String getStringResponse(Runnable doGet) {
        try {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);
            doGet.run();
            return stringWriter.getBuffer().toString().trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkAddProduct(Product product) {
        when(request.getParameter("name")).thenReturn(product.getName());
        when(request.getParameter("price")).thenReturn(String.valueOf(product.getPrice()));
        assertEquals("OK", getStringResponse(() -> {
            try {
                new AddProductServlet(databaseUrl).doGet(request, response);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }));
    }

    private String cropHtmlTags(String response) {
        response = response.replaceAll("\n", "").trim();
        assertTrue(response.startsWith(EXPECTED_START));
        assertTrue(response.endsWith(EXPECTED_END));
        return response.substring(EXPECTED_START.length(), response.length() - EXPECTED_END.length()).trim();
    }

    private Map<Product, Integer> getProductsFromHtmlAnswer(String answer) {
        String[] productStrings = cropHtmlTags(answer).split("</br>");

        Map<Product, Integer> products = new HashMap<>();
        for (String productString : productStrings) {
            if (productString.trim().isEmpty()) {
                continue;
            }
            String[] data = productString.split("\t");
            Product product = new Product(data[0], Integer.parseInt(data[1]));
            Integer oldCount = products.getOrDefault(product, 0);
            products.put(product, oldCount + 1);
        }
        return products;
    }

    private void checkGetProducts(Map<Product, Integer> products) {
        String getProductsResponse = getStringResponse(() -> {
            try {
                new GetProductsServlet(databaseUrl).doGet(request, response);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        assertEquals(products, getProductsFromHtmlAnswer(getProductsResponse));
    }

    Integer getMinMaxResult(String response) {
        String stringWithPrice = cropHtmlTags(response).replace("</br>", "");
        // <h1>Product with *** price: </h1> \t price
        String[] data = stringWithPrice.split("\t");
        assertEquals(2, data.length);
        return Integer.parseInt(data[1]);
    }

    Integer getCountOrSumResult(String response) {
        String stringWithPrice = cropHtmlTags(response).replace("</br>", "");
        // Summary price: result
        // or
        // Number of products: result
        String[] data = stringWithPrice.split(" ");
        return Integer.parseInt(data[data.length - 1]);
    }

    private String getQueryResponse(String command) {
        when(request.getParameter("command")).thenReturn(command);
        return getStringResponse(() -> {
            try {
                new QueryServlet(databaseUrl).doGet(request, response);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).replace("\n", "");
    }

    private void checkMax(Integer result) {
        assertEquals(result, getMinMaxResult(getQueryResponse("max")));
    }

    private void checkMin(Integer result) {
        assertEquals(result, getMinMaxResult(getQueryResponse("min")));
    }

    private void checkCount(Integer result) {
        assertEquals(result, getCountOrSumResult(getQueryResponse("count")));
    }

    private void checkSum(Integer result) {
        assertEquals(result, getCountOrSumResult(getQueryResponse("sum")));
    }

    private Product makeRandomProduct() {
        return new Product(randomString(random.nextInt(MAX_RANDOM_STRING_LENGTH - 1) + 1), random.nextInt(MAX_RANDOM_PRICE));
    }

    private Map<Product, Integer> addRandomProducts(int count) {
        Map<Product, Integer> countTable = new HashMap<>();
        for (int i = 0; i < count; i++) {
            Product product = makeRandomProduct();
            checkAddProduct(product);
            Integer oldCount = countTable.getOrDefault(product, 0);
            countTable.put(product, oldCount + 1);
        }
        return countTable;
    }

    @Test
    public void testAddProduct() {
        Product apple = new Product("apple", 10);
        Product banana = new Product("banana", 20);

        checkAddProduct(apple);
        checkGetProducts(Map.of(apple, 1));
        checkAddProduct(banana);
        checkGetProducts(Map.of(apple, 1, banana, 1));
    }

    @Test
    public void testAddSameProduct() {
        Product apple = new Product("apple", 10);

        checkAddProduct(apple);
        checkAddProduct(apple);
        checkGetProducts(Map.of(apple, 2));
    }

    @Test
    public void testMaxOf2Products() {
        Product apple = new Product("apple", 10);
        Product banana = new Product("banana", 20);

        checkAddProduct(apple);
        checkAddProduct(banana);
        checkMax(Math.max(banana.getPrice(), apple.getPrice()));
    }

    @Test
    public void testMinOf2Products() {
        Product apple = new Product("apple", 10);
        Product banana = new Product("banana", 20);

        checkAddProduct(apple);
        checkAddProduct(banana);
        checkMin(Math.min(banana.getPrice(), apple.getPrice()));
    }

    @Test
    public void testSumOf2Products() {
        Product apple = new Product("apple", 10);
        Product banana = new Product("banana", 20);

        checkAddProduct(apple);
        checkAddProduct(banana);
        checkSum(apple.getPrice() + banana.getPrice());
    }

    @Test
    public void testCountOf2Products() {
        Product apple = new Product("apple", 10);
        Product banana = new Product("banana", 20);

        checkAddProduct(apple);
        checkAddProduct(banana);
        checkCount(2);
    }

    @Test
    public void testAllOf2Products() {
        Product apple = new Product("apple", 10);
        Product banana = new Product("banana", 20);

        checkAddProduct(apple);
        checkAddProduct(banana);
        checkGetProducts(Map.of(apple, 1, banana, 1));
        checkMax(Math.max(banana.getPrice(), apple.getPrice()));
        checkMin(Math.min(banana.getPrice(), apple.getPrice()));
        checkSum(apple.getPrice() + banana.getPrice());
        checkCount(2);
    }

    @Test
    public void checkEmpty() {
        checkGetProducts(Map.of());
        checkSum(0);
        checkCount(0);
    }

    @Test
    public void testRandomAdd() {
        for (int i = 0; i < RANDOM_TESTS_COUNT; i++) {
            createDatabase();
            checkGetProducts(addRandomProducts(random.nextInt(MAX_RANDOM_PRODUCTS_COUNT)));
        }
    }

    @Test
    public void testRandomMax() {
        for (int i = 0; i < RANDOM_TESTS_COUNT; i++) {
            createDatabase();
            Map<Product, Integer> products = addRandomProducts(random.nextInt(MAX_RANDOM_PRODUCTS_COUNT - 1) + 1);
            Product bestProduct = Collections.max(products.keySet(), Comparator.comparingInt(Product::getPrice));
            checkMax(bestProduct.getPrice());
        }
    }

    @Test
    public void testRandomMin() {
        for (int i = 0; i < RANDOM_TESTS_COUNT; i++) {
            createDatabase();
            Map<Product, Integer> products = addRandomProducts(random.nextInt(MAX_RANDOM_PRODUCTS_COUNT - 1) + 1);
            Product bestProduct = Collections.min(products.keySet(), Comparator.comparingInt(Product::getPrice));
            checkMin(bestProduct.getPrice());
        }
    }

    @Test
    public void testRandomCount() {
        for (int i = 0; i < RANDOM_TESTS_COUNT; i++) {
            createDatabase();
            int count = random.nextInt(MAX_RANDOM_PRODUCTS_COUNT);
            addRandomProducts(count);
            checkCount(count);
        }
    }

    @Test
    public void testRandomSum() {
        for (int i = 0; i < RANDOM_TESTS_COUNT; i++) {
            createDatabase();
            Map<Product, Integer> products = addRandomProducts(random.nextInt(MAX_RANDOM_PRODUCTS_COUNT));
            checkSum(products.entrySet().stream().mapToInt((entry) -> (entry.getKey().getPrice() * entry.getValue())).sum());
        }
    }
}
