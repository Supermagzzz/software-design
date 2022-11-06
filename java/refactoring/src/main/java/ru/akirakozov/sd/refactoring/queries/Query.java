package ru.akirakozov.sd.refactoring.queries;

import ru.akirakozov.sd.refactoring.database.Database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class Query {
    protected final Database database;

    public Query(Database database) {
        this.database = database;
    }

    public abstract String processQuery(HttpServletRequest request);
}
