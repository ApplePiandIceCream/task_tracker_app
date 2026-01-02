# Task Management Application

A simple full-stack task management application built as part of a technical assessment.  
The project consists of:

- A **Java Spring Boot backend** providing a RESTful API
- A **static frontend** built with **HTML, CSS, JavaScript, and Bootstrap**
- A **persistent H2 file-based database**

This application allows users to  create tasks.

---

## 🧰 Tech Stack

### **Backend**
- Java 17+
- Spring Boot 3
- Maven
- H2 Database (file mode, persistent)
- Spring Web / Validation

### **Frontend**
- HTML5  
- CSS3  
- JavaScript (vanilla)
- Bootstrap 5

---

# 🚀 Getting Started

## 1️⃣ Run the Backend (Spring Boot)

### **Prerequisites**
You must have:
- JDK **17+**
- Maven installed (so the `mvn` command works)

### **Start the backend**
From the backend folder:

```bash
mvn spring-boot:run
```

The API will be available at:

```
http://localhost:8080
```

### **Main API Endpoint**
```
http://localhost:8080/api/tasks
```

---

## 🗄️ Database (H2 File Mode)

The backend uses a persistent file-based H2 database, configured using:

```
spring.datasource.url=jdbc:h2:file:./data/tasksdb
```

This creates files such as:

```
/data/tasksdb.mv.db
```

Your data **persists across server restarts**.

You may access the database console at:

```
http://localhost:8080/h2-console
```

(Use the same JDBC URL as above.)

---

## 2️⃣ Run the Frontend (HTML / JS)

The frontend is static — **no Node, npm, or build tools required**.

### **How to run it**
1. Ensure the backend is running  
2. Open the frontend folder  
3. Launch the app by opening:

```
index.html
```

Simply double-click the file or open it in your browser.

It communicates with the backend using `fetch()` calls to:

```
http://localhost:8080/api/tasks
```

---

# 📡 API Overview


### **POST /api/tasks**
Create a new task  
Requires valid JSON body and passes server-side validation

---

# 🛠️ Backend Features

### ✔️ **RESTful CRUD API**

### ✔️ **Validation**
Uses Jakarta Bean Validation:
- `@NotNull`
- `@Size`
- `@Pattern`
- etc.

### ✔️ **Global Exception Handling**
Custom handler for:
- Validation errors (400)
- Malformed JSON
- Generic server exceptions

---

# 📁 Project Structure

```
junior-dev-task/
│
├── junior-dev-task-frontend/
│   ├── index.html
│   ├── styles.css
│   └── script.js
│
└── junior-dev-task-backend/
    ├── src/
    ├── pom.xml
    └── application.properties
```

---

# 📝 Notes

- This application is intentionally minimal and designed to demonstrate clear code structure, API design, and full-stack integration.
- No personal information is included, in line with Civil Service **name-blind** application requirements.

---

# ✔️ Ready for Review

The project is now fully runnable on any machine with Java 17+ and Maven, with no external dependencies.

