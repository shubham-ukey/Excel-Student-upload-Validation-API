# Excel Student Upload & Validation API

A Spring Boot REST API that accepts an Excel `.xlsx` file, validates student records row-by-row, inserts only valid records into MySQL, and returns validation errors for invalid rows.

## 🚀 Technologies

* Java
* Spring Boot
* Spring Data JPA / Hibernate
* MySQL
* Apache POI
* Maven
* Postman

## 📌 API

**POST**

```text
http://localhost:8082/api/students/upload-excel
```

### Postman

Select:

```text
Body → form-data
```

| Key  | Type | Value               |
| ---- | ---- | ------------------- |
| file | File | Select `.xlsx` file |

## 📄 Excel Format

Excel must contain these columns:

```text
student_name
email
mobile
course
city
fees
```

## ✅ Validation

The API validates:

* Student name – required, minimum 3 characters
* Email – required, valid format and unique
* Mobile – exactly 10 digits and unique
* Course – Java, Python, Testing, Data Analytics
* City – required
* Fees – numeric and greater than 0

## 🔄 Processing

```text
Excel Upload
     ↓
Read Excel
     ↓
Validate Each Row
     ↓
 ┌───────────────┐
 │ Valid?        │
 └──────┬────────┘
    Yes │ No
       ↓   ↓
    Save   Skip
      DB   Row
       ↓   ↓
   Processing Response
```

Valid rows are inserted into the database, while invalid rows are skipped and their errors are returned in the response.

## 🗄️ Database

**Database:** MySQL
**Table:** `students`

Fields:

```text
id
student_name
email
mobile
course
city
fees
created_at
```

## 📁 Project Structure

```text
controller
dto
entity
exception
repository
service
```

## 🧪 Sample Response

```json
{
  "message": "Excel processing completed",
  "total_rows": 14,
  "inserted_count": 8,
  "failed_count": 6,
  "errors": []
}
```

> `inserted_count` and `failed_count` depend on the uploaded Excel data and existing database records.

## 🎯 Purpose

This project demonstrates **Excel file upload, row-wise validation, duplicate checking, REST API development, and MySQL database integration using Spring Boot**.

## 👨‍💻 Author

**Shubham Ukey**
