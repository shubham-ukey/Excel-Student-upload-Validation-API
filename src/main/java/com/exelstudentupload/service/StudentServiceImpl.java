package com.exelstudentupload.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.exelstudentupload.dto.ExcelUploadResponse;
import com.exelstudentupload.dto.ValidationError;
import com.exelstudentupload.entity.Student;
import com.exelstudentupload.repository.StudentRepository;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    private static final List<String> ALLOWED_COURSES = Arrays.asList(
            "Java",
            "Python",
            "Testing",
            "Data Analytics"
    );

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
            );

    @Override
    public ExcelUploadResponse processExcel(MultipartFile file) {

        // Step 1: Validate file
        validateFile(file);

        List<ValidationError> validationErrors = new ArrayList<>();

        Set<String> uploadedEmails = new HashSet<>();
        Set<String> uploadedMobiles = new HashSet<>();

        int totalRows = 0;
        int insertedCount = 0;
        int failedCount = 0;

        try (
                InputStream inputStream = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)
        ) {

            // Check sheet
            if (workbook.getNumberOfSheets() == 0) {

                throw new IllegalArgumentException(
                        "Excel file does not contain any sheet"
                );
            }

            Sheet sheet = workbook.getSheetAt(0);

            DataFormatter formatter = new DataFormatter();

            // Check header
            validateHeader(sheet, formatter);

            // Start from row 1 because row 0 is header
            for (int rowIndex = 1;
                 rowIndex <= sheet.getLastRowNum();
                 rowIndex++) {

                Row row = sheet.getRow(rowIndex);

                // Skip completely empty rows
                if (isEmptyRow(row, formatter)) {
                    continue;
                }

                totalRows++;

                int excelRowNumber = rowIndex + 1;

                // Read Excel cells
                String studentName =
                        getCellValue(row, 0, formatter);

                String email =
                        getCellValue(row, 1, formatter);

                String mobile =
                        getCellValue(row, 2, formatter);

                String course =
                        getCellValue(row, 3, formatter);

                String city =
                        getCellValue(row, 4, formatter);

                String feesValue =
                        getCellValue(row, 5, formatter);

                // Validate row
                List<String> errors = validateRow(
                        studentName,
                        email,
                        mobile,
                        course,
                        city,
                        feesValue,
                        uploadedEmails,
                        uploadedMobiles
                );

                // If validation errors found
                if (!errors.isEmpty()) {

                    validationErrors.add(
                            new ValidationError(
                                    excelRowNumber,
                                    studentName,
                                    email,
                                    mobile,
                                    errors
                            )
                    );

                    failedCount++;

                    // Don't insert invalid row
                    continue;
                }

                // Convert fees
                BigDecimal fees =
                        new BigDecimal(feesValue.trim());

                // Create Student
                Student student = new Student();

                student.setStudentName(
                        studentName.trim()
                );

                student.setEmail(
                        email.trim().toLowerCase()
                );

                student.setMobile(
                        mobile.trim()
                );

                student.setCourse(
                        course.trim()
                );

                student.setCity(
                        city.trim()
                );

                student.setFees(fees);

                student.setCreatedAt(
                        LocalDateTime.now()
                );

                // Save valid student
                studentRepository.save(student);

                // Add successful records to uploaded sets
                uploadedEmails.add(
                        email.trim().toLowerCase()
                );

                uploadedMobiles.add(
                        mobile.trim()
                );

                insertedCount++;
            }

        } catch (IllegalArgumentException e) {

            throw e;

        } catch (IOException e) {

            throw new IllegalArgumentException(
                    "Unable to read Excel file. Please upload a valid .xlsx file."
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unexpected error while processing Excel file"
            );
        }

        // No data rows
        if (totalRows == 0) {

            throw new IllegalArgumentException(
                    "Excel file does not contain any student data"
            );
        }

        return new ExcelUploadResponse(
                "Excel processing completed",
                totalRows,
                insertedCount,
                failedCount,
                validationErrors
        );
    }

    // ---------------------------------------------------------
    // FILE VALIDATION
    // ---------------------------------------------------------

    private void validateFile(MultipartFile file) {

        if (file == null) {

            throw new IllegalArgumentException(
                    "File is required"
            );
        }

        if (file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Uploaded file is empty"
            );
        }

        String fileName =
                file.getOriginalFilename();

        if (fileName == null ||
                !fileName.toLowerCase().endsWith(".xlsx")) {

            throw new IllegalArgumentException(
                    "Only .xlsx files are allowed"
            );
        }
    }

    // ---------------------------------------------------------
    // HEADER VALIDATION
    // ---------------------------------------------------------

    private void validateHeader(
            Sheet sheet,
            DataFormatter formatter) {

        Row headerRow = sheet.getRow(0);

        if (headerRow == null) {

            throw new IllegalArgumentException(
                    "Excel header row is missing"
            );
        }

        String[] expectedHeaders = {
                "student_name",
                "email",
                "mobile",
                "course",
                "city",
                "fees"
        };

        for (int i = 0; i < expectedHeaders.length; i++) {

            String actualHeader =
                    getCellValue(
                            headerRow,
                            i,
                            formatter
                    );

            if (!expectedHeaders[i].equalsIgnoreCase(
                    actualHeader.trim())) {

                throw new IllegalArgumentException(
                        "Invalid Excel header. Expected: student_name, email, mobile, course, city, fees"
                );
            }
        }
    }

    // ---------------------------------------------------------
    // ROW VALIDATION
    // ---------------------------------------------------------

    private List<String> validateRow(
            String studentName,
            String email,
            String mobile,
            String course,
            String city,
            String feesValue,
            Set<String> uploadedEmails,
            Set<String> uploadedMobiles) {

        List<String> errors = new ArrayList<>();

        // -----------------------------------------------------
        // STUDENT NAME
        // -----------------------------------------------------

        if (studentName == null ||
                studentName.trim().isEmpty()) {

            errors.add(
                    "Student name is mandatory"
            );

        } else if (studentName.trim().length() < 3) {

            errors.add(
                    "Student name must contain at least 3 characters"
            );
        }

        // -----------------------------------------------------
        // EMAIL
        // -----------------------------------------------------

        if (email == null ||
                email.trim().isEmpty()) {

            errors.add(
                    "Email is mandatory"
            );

        } else {

            String normalizedEmail =
                    email.trim().toLowerCase();

            if (!EMAIL_PATTERN.matcher(
                    normalizedEmail
            ).matches()) {

                errors.add(
                        "Invalid email format"
                );

            } else {

                // Duplicate in Excel
                if (uploadedEmails.contains(
                        normalizedEmail)) {

                    errors.add(
                            "Email already exists in uploaded Excel file"
                    );
                }

                // Duplicate in database
                if (studentRepository.existsByEmail(
                        normalizedEmail)) {

                    errors.add(
                            "Email already exists in database"
                    );
                }
            }
        }

        // -----------------------------------------------------
        // MOBILE
        // -----------------------------------------------------

        if (mobile == null ||
                mobile.trim().isEmpty()) {

            errors.add(
                    "Mobile number is mandatory"
            );

        } else {

            String normalizedMobile =
                    mobile.trim();

            // IMPORTANT: Correct regex
            if (!normalizedMobile.matches("\\d+")) {

                errors.add(
                        "Mobile number must contain digits only"
                );

            } else if (normalizedMobile.length() != 10) {

                errors.add(
                        "Mobile number must contain exactly 10 digits"
                );

            } else {

                // Duplicate in Excel
                if (uploadedMobiles.contains(
                        normalizedMobile)) {

                    errors.add(
                            "Mobile number already exists in uploaded Excel file"
                    );
                }

                // Duplicate in database
                if (studentRepository.existsByMobile(
                        normalizedMobile)) {

                    errors.add(
                            "Mobile number already exists in database"
                    );
                }
            }
        }

        // -----------------------------------------------------
        // COURSE
        // -----------------------------------------------------

        if (course == null ||
                course.trim().isEmpty()) {

            errors.add(
                    "Course is mandatory"
            );

        } else if (!ALLOWED_COURSES.contains(
                course.trim())) {

            errors.add(
                    "Course must be one of: Java, Python, Testing, Data Analytics"
            );
        }

        // -----------------------------------------------------
        // CITY
        // -----------------------------------------------------

        if (city == null ||
                city.trim().isEmpty()) {

            errors.add(
                    "City is mandatory"
            );
        }

        // -----------------------------------------------------
        // FEES
        // -----------------------------------------------------

        if (feesValue == null ||
                feesValue.trim().isEmpty()) {

            errors.add(
                    "Fees is mandatory"
            );

        } else {

            try {

                BigDecimal fees =
                        new BigDecimal(
                                feesValue.trim()
                        );

                if (fees.compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

                    errors.add(
                            "Fees must be greater than 0"
                    );
                }

            } catch (NumberFormatException e) {

                errors.add(
                        "Fees must be numeric"
                );
            }
        }

        return errors;
    }

    // ---------------------------------------------------------
    // GET CELL VALUE
    // ---------------------------------------------------------

    private String getCellValue(
            Row row,
            int columnIndex,
            DataFormatter formatter) {

        if (row == null) {
            return "";
        }

        Cell cell =
                row.getCell(columnIndex);

        if (cell == null) {
            return "";
        }

        return formatter
                .formatCellValue(cell)
                .trim();
    }

    // ---------------------------------------------------------
    // EMPTY ROW CHECK
    // ---------------------------------------------------------

    private boolean isEmptyRow(
            Row row,
            DataFormatter formatter) {

        if (row == null) {
            return true;
        }

        for (int i = 0; i < 6; i++) {

            String value =
                    getCellValue(
                            row,
                            i,
                            formatter
                    );

            if (!value.isBlank()) {
                return false;
            }
        }

        return true;
    }
}