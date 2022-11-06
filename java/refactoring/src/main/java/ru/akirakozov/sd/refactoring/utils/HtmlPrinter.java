package ru.akirakozov.sd.refactoring.utils;

import ru.akirakozov.sd.refactoring.data.Product;

public class HtmlPrinter {
    private final static String EXPECTED_START = "<html><body>";
    private final static String EXPECTED_END = "</body></html>";

    private final StringBuilder builder = new StringBuilder();

    public HtmlPrinter() {
        println(EXPECTED_START);
    }

    public void print(String s) {
        builder.append(s);
    }

    public void println() {
        builder.append(System.lineSeparator());
    }

    public void println(String s) {
        print(s);
        println();
    }

    public void printNextLine() {
        print("</br>");
    }

    public void printProduct(Product product) {
        print(product.getName() + "\t" + product.getPrice());
        printNextLine();
        println();
    }

    public void printHeader(String s) {
        print("<h1>");
        print(s);
        print("</h1>");
    }

    public String get() {
        println(EXPECTED_END);
        return builder.toString();
    }
}
