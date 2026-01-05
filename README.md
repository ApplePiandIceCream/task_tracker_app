# 📝 Task Manager

![Java](https://img.shields.io/badge/Java-21-blue) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1-green) ![Bootstrap](https://img.shields.io/badge/Bootstrap-5-purple)

A lightweight **Task Manager** web application featuring a RESTful API backend and a dynamic, responsive frontend. This project demonstrates clean CRUD (Create, Read, Update, Delete) implementation and client-side responsive filtering. 

---

## ⚡ Features

- **CRUD operations**: Create, Read, Update, Delete tasks via a REST API
- **Inline editing**: Edit tasks directly in the task list without navigating to a new page
- **Filtering & sorting**:
  - Search by task title
  - Sort by deadline (soonest/latest)
  - Filter by status (Pending, In Progress, Awaiting Response, Completed)
- **Responsive UI**: Bootstrap cards for tasks, works on desktop and mobile
- **Validation**: Client-side validation for deadlines and required fields
- **Confirmation prompts** for deletion

---

## 🧰 Tech Stack

### **Backend**
- Java 21 
- Spring Boot 3 
- Maven 
- Spring Data JPA 
- H2 Database
- Jakarta validation
- Deployment through Railway (web service)

### **Frontend**
- HTML5  
- CSS3  
- JavaScript (vanilla)
- Bootstrap 5

### 📡 API Reference
The backend exposes the following endpoints at /api/tasks:
- GET: /api/tasks (Retrieve all tasks)
- POST: /api/tasks (Create a new task- Validates JSON body)
- PUT: /api/tasks/{id} (Update an existing task by ID) 
- DELETE: /api/tasks/{id} (Remove a task by ID)

---

# 🚀 Getting Started

Live Demo: https://applepiandicecream.github.io/task_tracker_app/ 

Backend: Hosted on Railway (Always-on / No wake-up delay)- using railway removes the cold start delay that other free tiers have.  

## OR: 

## 1️⃣ Run the Backend (Spring Boot)

### **Prerequisites**
You must have:
- JDK **17+**
- Maven installed (so the `mvn` command works)

### **Start the backend**

```bash
cd task_tracker_backend
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
2. Locate index.html in the project root directory  
2. Launch the app by opening:

```
index.html
```

Simply double-click the file or open it in your browser.

It communicates with the backend using `fetch()` calls to:

```
http://localhost:8080/api/tasks
```

---

# 📁 Project Structure

```
task_tracker_app/
├── task_tracker_backend/   
│   ├── src/
│   ├── pom.xml
│   └── data/               
├── index.html              
├── script.js               
├── style.css                            
└── README.md
```

## 🛠️ Key Implementation Details
- Global Exception Handling: The backend includes a custom handler to manage validation errors (400) and malformed requests, returning clear error messages to the UI.

- CORS Configuration: Enabled via @CrossOrigin to allow the frontend to communicate with the API while running on different local environments.

- Separation of Concerns: The project follows a clean architecture, separating the database layer (JPA), the business logic (Controller), and the presentation layer (Vanilla JS).

---

## 📝 Author
Created as a demonstration of full-stack proficiency, focusing on clean code, RESTful principles, and responsive UI design.

