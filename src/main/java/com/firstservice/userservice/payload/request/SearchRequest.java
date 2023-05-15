package com.firstservice.userservice.payload.request;

import javax.annotation.sql.DataSourceDefinition;
import java.util.List;


public class SearchRequest {
    private List<String> columns;

    public SearchRequest() {
    }

    public SearchRequest(List<String> columns) {
        this.columns = columns;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}
