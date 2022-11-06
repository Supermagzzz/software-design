package ru.akirakozov.sd.refactoring.utils;

public class HtmlPrinter {
    private final static String EXPECTED_START = "<html><body>";
    private final static String EXPECTED_END = "</body></html>";

    private final StringBuilder builder = new StringBuilder();

    public HtmlPrinter() {
        println(EXPECTED_START);
    }

    public void println(String s) {
        builder.append(s).append(System.lineSeparator());
    }

    public String get() {
        println(EXPECTED_END);
        return builder.toString();
    }
}
