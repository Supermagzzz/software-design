package ru.akirakozov.sd.refactoring.queries;

import ru.akirakozov.sd.refactoring.database.Database;

import javax.servlet.http.HttpServletRequest;

public abstract class Query {
    protected final Database database;

    public Query(Database database) {
        this.database = database;
    }

    public abstract String makeResponse(HttpServletRequest request);
}
