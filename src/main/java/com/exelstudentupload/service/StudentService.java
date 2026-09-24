package com.exelstudentupload.service;

import org.springframework.web.multipart.MultipartFile;

import com.exelstudentupload.dto.ExcelUploadResponse;

public interface StudentService {

    ExcelUploadResponse processExcel(MultipartFile file);
}