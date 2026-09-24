📊 Excel Student Upload & Validation API

A Spring Boot REST API that allows users to upload an Excel ".xlsx" file, validates every student record row-by-row, saves only valid records into a MySQL database, and returns detailed validation errors for invalid rows.

This project is built as a backend practical task using Spring Boot, JPA, MySQL, Apache POI, and Postman.

---

🚀 Project Overview

The API accepts an Excel file through a REST endpoint.

The processing flow is:

Excel File
    ↓
Upload using Postman
    ↓
Read Excel using Apache POI
    ↓
Validate Every Row
    ↓
 ┌───────────────────────┐
 │ Is the row valid?     │
 └───────────┬───────────┘
             │
       ┌─────┴─────┐
       ↓           ↓
     YES           NO
       ↓           ↓
 Save to DB    Skip the row
       ↓           ↓
       └─────┬─────┘
             ↓
 Return Processing Result

Key Feature

If an Excel file contains both valid and invalid records:

- ✅ Valid records are inserted into the database.
- ❌ Invalid records are skipped.
- 📝 Validation errors are returned row-wise.
- 🔄 Processing does not stop because of an invalid row.

---

🛠️ Technologies Used

Technology| Purpose
Java| Programming Language
Spring Boot| REST API Development
Spring Web| REST Controllers
Spring Data JPA| Database Operations
Hibernate| ORM
MySQL| Database
Apache POI| Excel File Reading
Maven| Dependency Management
Postman| API Testing
Lombok| Optional boilerplate reduction

---

📁 Project Structure

src
└── main
    ├── java
    │   └── com
    │       └── exelstudentupload
    │
    │           ├── controller
    │           │   └── StudentController.java
    │           │
    │           ├── dto
    │           │   ├── ExcelUploadResponse.java
    │           │   └── ValidationError.java
    │           │
    │           ├── entity
    │           │   └── Student.java
    │           │
    │           ├── exception
    │           │   └── GlobalExceptionHandler.java
    │           │
    │           ├── repository
    │           │   └── StudentRepository.java
    │           │
    │           └── service
    │               ├── StudentService.java
    │               └── StudentServiceImpl.java
    │
    └── resources
        └── application.properties

---

🧩 Architecture

The application follows a layered Spring Boot architecture:

Postman
   │
   ▼
Controller
   │
   ▼
Service
   │
   ├── Excel Reading
   ├── Validation
   ├── Duplicate Checking
   └── Data Processing
   │
   ▼
Repository
   │
   ▼
MySQL Database

Controller

Receives the Excel file through the REST API.

Service

Contains the main business logic:

- Excel validation
- Row reading
- Field validation
- Duplicate checking
- Student object creation
- Database insertion
- Error collection

Repository

Uses Spring Data JPA to communicate with MySQL.

Entity

Represents the "students" database table.

DTO

Used for sending structured API responses.

Exception Handler

Handles invalid requests and unexpected server errors.

---

📄 Excel Format

The uploaded Excel file must contain these columns:

student_name
email
mobile
course
city
fees

Example:

student_name| email| mobile| course| city| fees
Amit Sharma| amit.sharma@gmail.com| 9876543210| Java| Nagpur| 45000
Priya Patil| priya.patil@gmail.com| 9876543211| Python| Pune| 40000
Rahul Verma| wrong-email| 9876543212| Testing| Mumbai| 35000

Important

Only ".xlsx" files are accepted.

The mobile column should preferably be formatted as Text in Excel so that mobile numbers are read correctly.

---

✅ Validation Rules

1. Student Name

- Required
- Cannot be empty
- Cannot contain only spaces
- Minimum 3 characters

Example:

Amit Sharma       ✅
AB                ❌
                   ❌

---

2. Email

- Required
- Must have a valid email format
- Must be unique in the database
- Must be unique within the uploaded Excel file

Example:

amit.sharma@gmail.com       ✅
wrong-email                ❌

---

3. Mobile

- Required
- Must contain digits only
- Must contain exactly 10 digits
- Must be unique in the database
- Must be unique within the uploaded Excel file

Example:

9876543210       ✅
98765            ❌
98AB543210       ❌

---

4. Course

Only the following courses are allowed:

Java
Python
Testing
Data Analytics

Example:

Java              ✅
Python            ✅
React             ❌
PHP               ❌

---

5. City

- Required
- Cannot be empty
- Cannot contain only spaces

Example:

Nagpur       ✅
Pune         ✅
              ❌

---

6. Fees

- Required
- Must be numeric
- Must be greater than "0"

Example:

45000       ✅
50000.50    ✅
0           ❌
-5000       ❌
ABC         ❌

---

🗄️ Database Structure

Database:

worksphere_db

Table:

students

«Update the database name in "application.properties" if you are using a different MySQL database.»

Table Columns

Column| Type| Constraint
id| BIGINT| Primary Key, Auto Increment
student_name| VARCHAR| NOT NULL
email| VARCHAR| NOT NULL, UNIQUE
mobile| VARCHAR| NOT NULL, UNIQUE
course| VARCHAR| NOT NULL
city| VARCHAR| NOT NULL
fees| DECIMAL| NOT NULL
created_at| TIMESTAMP| NOT NULL

---

⚙️ Configuration

Example "application.properties":

spring.application.name=ExcelStudentUpload

server.port=8082

spring.datasource.url=jdbc:mysql://localhost:3306/worksphere_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

Replace:

YOUR_PASSWORD

with your MySQL password.

---

📦 Required Dependencies

The project requires dependencies for:

Spring Web
Spring Data JPA
MySQL Driver
Apache POI
Lombok

Apache POI is used to read ".xlsx" Excel files.

Example Maven dependency:

<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.4.1</version>
</dependency>

---

▶️ How to Run the Project

Step 1 — Clone the Repository

git clone YOUR_GITHUB_REPOSITORY_URL

---

Step 2 — Open the Project

Open the project in:

Eclipse

or

Spring Tool Suite

or

IntelliJ IDEA

---

Step 3 — Configure MySQL

Create the database:

CREATE DATABASE worksphere_db;

Then configure:

spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

---

Step 4 — Run Spring Boot Application

Run the main Spring Boot application.

The application will start on:

http://localhost:8082

---

🧪 API Testing Using Postman

Upload Excel

Method

POST

URL

http://localhost:8082/api/students/upload-excel

Body

Select:

Body → form-data

Add:

Key| Type| Value
file| File| Select ".xlsx" file

Example:

Key: file
Type: File
Value: student_upload_test_data.xlsx

Important

Do not manually set:

Content-Type: application/json

For this API, Postman should send:

multipart/form-data

---

📤 Sample Excel Data

Example test data:

Row| Student| Email| Mobile| Course| City| Fees
1| Amit Sharma| amit.sharma@gmail.com| 9876543210| Java| Nagpur| 45000
2| Priya Patil| priya.patil@gmail.com| 9876543211| Python| Pune| 40000
3| Rahul Verma| wrong-email| 9876543212| Testing| Mumbai| 35000
4| Sneha Joshi| sneha.joshi@gmail.com| 98765| Data Analytics| Nashik| 50000
5| Empty| blank.name@gmail.com| 9876543213| Java| Nagpur| 30000
6| Rohan Gupta| rohan.gupta@gmail.com| 9876543214| PHP| Amravati| 25000
7| Neha Singh| amit.sharma@gmail.com| 9876543215| Java| Delhi| 42000
8| Vikas More| vikas.more@gmail.com| 9876543210| Python| Akola| 38000
9| Pooja Kale| pooja.kale@gmail.com| 9876543216| Testing| Wardha| 0
10| Karan Yadav| karan.yadav@gmail.com| 9876543217| React| Bhandara| 45000
11| Anjali Rao| anjali.rao@gmail.com| 9876543218| Java| Empty| 32000
12| Suresh Pawar| suresh.pawar@gmail.com| 9876543219| Python| Gondia| -5000
13| Meena Shah| meena.shah@gmail.com| 98765432120| Testing| Nagpur| 36000
14| Arjun Das| arjun.das@gmail.com| 98AB543210| Data Analytics| Pune| 55000

This dataset intentionally contains both valid and invalid records to test the validation functionality.

---

📥 Sample API Response

After uploading the Excel file, the API returns a response similar to:

{
    "message": "Excel processing completed",
    "total_rows": 14,
    "inserted_count": 3,
    "failed_count": 11,
    "errors": [
        {
            "row": 4,
            "studentName": "Rahul Verma",
            "email": "wrong-email",
            "mobile": "9876543212",
            "errors": [
                "Invalid email format"
            ]
        },
        {
            "row": 5,
            "studentName": "Sneha Joshi",
            "email": "sneha.joshi@gmail.com",
            "mobile": "98765",
            "errors": [
                "Mobile number must contain exactly 10 digits"
            ]
        }
    ]
}

The exact "inserted_count" can change depending on the existing data in the database.

---

🔄 Processing Logic

For every Excel row:

Read Row
   ↓
Validate Student Name
   ↓
Validate Email
   ↓
Check Email Duplicate
   ↓
Validate Mobile
   ↓
Check Mobile Duplicate
   ↓
Validate Course
   ↓
Validate City
   ↓
Validate Fees
   ↓
Any Error?
 ┌───────┴───────┐
 YES             NO
 ↓                ↓
Add Error       Create Student
 ↓                ↓
Skip Row        Save to DB
 └───────┬────────┘
         ↓
     Next Row

---

🔐 Duplicate Validation

The API performs duplicate checking at two levels.

Database Duplicate

Excel Email
     ↓
Check MySQL
     ↓
Already exists?
     ↓
Return validation error

Excel Duplicate

Row 2 Email
     ↓
Store email in Set
     ↓
Row 7 has same email
     ↓
Duplicate detected
     ↓
Row 7 skipped

The same approach is used for mobile numbers.

---

❌ Error Handling

The API handles different types of errors.

Missing File

{
    "message": "File is required"
}

Empty File

{
    "message": "Uploaded file is empty"
}

Wrong File Type

{
    "message": "Only .xlsx files are allowed"
}

Missing Excel Data

{
    "message": "Excel file does not contain any student data"
}

Invalid Excel Header

{
    "message": "Invalid Excel header. Expected: student_name, email, mobile, course, city, fees"
}

---

🌐 API Details

Property| Value
Method| POST
Endpoint| "/api/students/upload-excel"
Port| "8082"
Content-Type| "multipart/form-data"
Parameter| "file"
File Type| ".xlsx"

---

📊 HTTP Status Codes

Status| Meaning
"200 OK"| Excel processed successfully, including partial success
"400 BAD REQUEST"| Missing/invalid/unreadable Excel input
"500 INTERNAL SERVER ERROR"| Unexpected server/database error

---

🎯 Project Requirements Covered

- [x] Excel ".xlsx" upload
- [x] REST API
- [x] Postman testing
- [x] Apache POI Excel processing
- [x] Row-wise validation
- [x] Multiple validation errors per row
- [x] Database validation
- [x] Excel duplicate validation
- [x] Valid rows inserted
- [x] Invalid rows skipped
- [x] MySQL database integration
- [x] JPA/Hibernate
- [x] Global exception handling
- [x] Detailed API response
- [x] No frontend required

---

💡 What I Learned From This Project

Through this project, I practiced:

- Building REST APIs using Spring Boot
- Handling "MultipartFile"
- Uploading files through Postman
- Reading Excel files using Apache POI
- Working with Excel rows and cells
- Implementing business validations
- Handling duplicate records
- Using "JpaRepository"
- Saving data into MySQL
- Creating DTO-based API responses
- Implementing global exception handling
- Processing partial-success requests
- Designing layered Spring Boot architecture

---

🔮 Future Enhancements

Possible future improvements:

- Add downloadable error Excel file
- Add pagination for student records
- Add GET API for students
- Add DELETE and UPDATE APIs
- Add authentication using Spring Security
- Add Swagger/OpenAPI documentation
- Add transaction management
- Add validation using Bean Validation
- Add frontend using React.js
- Add Excel export functionality
- Add logging using SLF4J/Logback

---

👨‍💻 Author

Shubham Ukey

Java Full Stack Developer

Skills Used in This Project

Java
Spring Boot
Spring Data JPA
Hibernate
MySQL
Apache POI
REST API
Postman
Maven

---

⭐ Project Purpose

This project was developed as a practical demonstration of Excel file processing, row-level validation, REST API development, and database integration using Spring Boot.

It demonstrates how a backend application can process large Excel datasets while allowing valid records to be stored even when other rows contain validation errors.