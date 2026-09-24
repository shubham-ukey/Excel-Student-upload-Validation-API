package com.exelstudentupload.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationError {

    private int row;
    private String studentName;
    private String email;
    private String mobile;
    private List<String> errors;

    public ValidationError() {
    }

    public ValidationError(
            int row,
            String studentName,
            String email,
            String mobile,
            List<String> errors) {

        this.row = row;
        this.studentName = studentName;
        this.email = email;
        this.mobile = mobile;
        this.errors = errors;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}