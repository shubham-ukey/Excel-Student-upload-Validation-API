package com.exelstudentupload.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExcelUploadResponse {

    private String message;

    @JsonProperty("total_rows")
    private int totalRows;

    @JsonProperty("inserted_count")
    private int insertedCount;

    @JsonProperty("failed_count")
    private int failedCount;

    private List<ValidationError> errors;

    public ExcelUploadResponse() {
    }

    public ExcelUploadResponse(
            String message,
            int totalRows,
            int insertedCount,
            int failedCount,
            List<ValidationError> errors) {

        this.message = message;
        this.totalRows = totalRows;
        this.insertedCount = insertedCount;
        this.failedCount = failedCount;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getInsertedCount() {
        return insertedCount;
    }

    public void setInsertedCount(int insertedCount) {
        this.insertedCount = insertedCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }
}