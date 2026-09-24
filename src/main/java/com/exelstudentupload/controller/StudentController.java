package com.exelstudentupload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.exelstudentupload.dto.ExcelUploadResponse;
import com.exelstudentupload.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/upload-excel")
    public ResponseEntity<ExcelUploadResponse> uploadExcel(
            @RequestParam("file") MultipartFile file) {

        ExcelUploadResponse response =
                studentService.processExcel(file);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}