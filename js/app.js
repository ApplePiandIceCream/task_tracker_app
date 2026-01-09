(function() {
    const token = sessionStorage.getItem("jwtToken");
    const path = window.location.pathname;
    const isAtHome = path.endsWith("/") || path.includes("index") || path.endsWith("task_tracker_app");

    if (isAtHome && !token) {
        window.location.href = "login.html";
    }
})();

// URL variables- local or deployed.  
let API_BASE_URL;

if (window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1" || window.location.protocol === "file:") {
    API_BASE_URL = "http://localhost:8080/api/tasks";
} else {
    API_BASE_URL = "https://tasktrackerapp-production-a6f9.up.railway.app/api/tasks";
}

let API_LOGIN_URL;
if (window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1" || window.location.protocol === "file:") {
    API_LOGIN_URL = "http://localhost:8080/api/auth/signin";
} else {
    API_LOGIN_URL = "https://tasktrackerapp-production-a6f9.up.railway.app/api/auth/signin";
}

let API_REGISTER_URL;
if (window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1" || window.location.protocol === "file:") {
    API_REGISTER_URL = "http://localhost:8080/api/auth/signup";
} else {
    API_REGISTER_URL = "https://tasktrackerapp-production-a6f9.up.railway.app/api/auth/signup";
}


//constants
const taskList = document.getElementById("task-list");
const successMessage = document.getElementById("successMessage");
const errorMessage = document.getElementById("errorMessage");

const filterSearch = document.getElementById("filterSearch");
const filterSort = document.getElementById("filterSort");
const filterStatus = document.getElementById("filterStatus");
const filterClear = document.getElementById("filterClear");

//status label formatting
const statusLabel = {
    "PENDING": "Pending",
    "IN_PROGRESS": "In Progress",
    "AWAITING_RESPONSE": "Awaiting Response",
    "COMPLETED": "Completed"
};


let editingTaskId = null;
let tasksAll = [];

//get URL path 
const path = window.location.pathname.split("/").pop();

//DOM CONTENT LOADED 
window.addEventListener("DOMContentLoaded", () => {
//remove protected from body- here only to prevent reloading when clicking on logo link
    document.body.classList.remove("protected");
    //update navigation based on logged in/out
    updateNav();
    //event listener for logout button 
    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", () => {
            sessionStorage.removeItem("jwtToken");
            sessionStorage.removeItem("username");

            alert("You have been logged out");

            window.location.href = "login.html";
        })
    }
    // Login page event listener
    if (path.includes("login")) {
        const loginForm = document.getElementById("login");
        if (loginForm) {
            loginForm.addEventListener("submit", async (e) => {
                e.preventDefault();
                //convert username to lower case for processing 
                const username = document.getElementById("username").value.toLowerCase();
                const password = document.getElementById("password").value;
                try {
                    const response = await fetch(API_LOGIN_URL, {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ username, password })
                    });

                    if (!response.ok) throw new Error("Login Failed");
                    //format data jwt token and store
                    const data = await response.text();
                    const cleanToken = data.replace(/^"|"$/g, '');

                    sessionStorage.setItem("jwtToken", cleanToken);
                    sessionStorage.setItem("username", username);

                    alert("Login successful!");
                    //redirect to index page
                    window.location.href = "index.html";

                } catch (err) {
                    const loginError = document.getElementById("loginError");
                    loginError.style.display = "block";
                    loginError.textContent = err.message;
                }
            });
        }
    }
    //registration path login and event listener
    if (path.includes("register")) {

        const registerForm = document.getElementById("registerF");

        if (registerForm) {
            registerForm.addEventListener("submit", async (e) => {
                e.preventDefault();
                const username = document.getElementById("username").value.toLowerCase();
                const password = document.getElementById("password").value;
                const passwordConfirm = document.getElementById("passwordConfirm").value;
                //error- notify user if username is less than 3 characters 
                if (username.length < 3) {
                    const usernameError = document.getElementById("usernameError");
                    usernameError.style.display = "block";
                    usernameError.textContent = "Username must be a minimum of 3 characters!"
                }
                //error- notify users if passwords don't match. 
                if (password !== passwordConfirm) {
                    const passwordError = document.getElementById("passwordError");
                    passwordError.style.display = "block";
                    passwordError.textContent = "Passwords must match!";
                    return;
                }

                try {
                    const response = await fetch(API_REGISTER_URL, {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ username, password })
                    });

                    if (!response.ok) throw new Error("Registration failed");

                    const msg = await response.text();
                    alert(msg);
                    //redirect to login if successfully registered
                    window.location.href = "login.html";
                }
                catch (err) {
                    const passwordError = document.getElementById("passwordError");
                    passwordError.style.display = "block";
                    passwordError.textContent = err.message;
                }
            });
        }
    };

    // main tasks page and event listeners
    if (path.includes("index") || path.endsWith("/") || path.endsWith("task_tracker_app")) {
        const form = document.getElementById("taskForm");
        const DeadlineInput = document.getElementById("deadline");

        //Get current date/ time
        const now = new Date();
        //convert current time to YY-MM-DDTHH:MM format
        const localISOTime = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
        //min value for date picker
        DeadlineInput.min = localISOTime;

        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            // Hide messages before processing 
            successMessage.classList.add("d-none");
            errorMessage.classList.add("d-none");

            //get value from datetime-liocal input
            const deadlineInput = document.getElementById("deadline");
            const deadlineVal = deadlineInput.value;

            //RegEx to check appropriate date format
            const datetimeRegex = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/;
            if (!datetimeRegex.test(deadlineVal)) {
                errorMessage.innerHTML = "Deadline must be in the format YYYY-MM-DDTHH:MM";
                errorMessage.classList.remove("d-none");
                return; // stop execution if validation fails
            }

            const finalDeadlineStr = deadlineVal; //validated string- used due to issues with debugging timedate format 

            // Collect form data as Task object
            const taskData = {
                title: document.getElementById("title").value.trim(),
                description: document.getElementById("description").value.trim(),
                status: document.getElementById("status").value,
                deadline: finalDeadlineStr
            };

            //call tasks API
            try {
                const method = editingTaskId ? "PUT" : "POST";
                const url = editingTaskId ? `${API_BASE_URL}/${editingTaskId}` : API_BASE_URL;

                const res = await fetch(url, {
                    method: method,
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": "Bearer " + sessionStorage.getItem("jwtToken")
                    },
                    body: JSON.stringify(taskData),
                });
                //read response 
                const responseBody = await res.json();

                if (!res.ok) {
                    // Error handling for backend validation
                    const messages = Object.values(responseBody).join("<br>");
                    errorMessage.innerHTML = messages;
                    errorMessage.classList.remove("d-none");
                    return;
                }
                // Sucessfully submitted
                successMessage.innerHTML = `
                    <strong>Task created successfully!</strong><br>
                    <div class="mt-2">
                        <strong>Title:</strong> ${responseBody.title}<br>
                        <strong>Description:</strong> ${responseBody.description}<br>
                        <strong>Status:</strong> ${responseBody.status}<br>
                        <strong>Deadline:</strong> ${new Date(responseBody.deadline).toLocaleString()}
                    </div>
                    `;
                successMessage.classList.remove("d-none");
                //clear form fields 
                editingTaskId = null;
                form.reset();
                await loadTasks();
                //catch network/ parsing errors 
            } catch (error) {
                console.error("Fetch or processing error:", error);
                errorMessage.innerText = "Something went wrong. Try again.";
                errorMessage.classList.remove("d-none");
            }
        });

        //call load tasks API
        loadTasks();
        //event listeners and empty values for search function
        filterSearch.addEventListener("input", applyFilter);
        filterSort.addEventListener("change", applyFilter);
        filterStatus.addEventListener("change", applyFilter);
        filterClear.addEventListener("click", () => {
            filterSearch.value = "";
            filterSort.value = "";
            filterStatus.value = "";
            renderTasks(tasksAll);
        });
    };
});



//Load tasks to pass to renderTasks() 
async function loadTasks() {
    const token = sessionStorage.getItem("jwtToken");
    if (!token) return;
    try {
        const res = await fetch(API_BASE_URL, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            }
        });
    if (res.status === 401 || res.status === 403) {
        handleAuthError();
        return;
    }
        //fetch JSON data 
    const tasks = await res.json();
    tasksAll = tasks;
    //render task data 
    renderTasks(tasks);
    } catch (err) {
        console.log("Error loading tasks ", err);
    }
};

//render tasks()- loops through all tasks from loadTasks() and formats and presents them. 
function renderTasks(tasks) {
    taskList.innerHTML = '';
    //if no tasks listed:
    if (tasks.length == 0) {
        taskList.textContent = "No tasks listed";
        return
    }
    //format each task into presentable (card) format- VIEW tasks 
    tasks.forEach(task => {
        const card = document.createElement("div");
        card.classList.add("card", "custom-card", "mb-3");

        const cardBody = document.createElement("div");
        cardBody.classList.add("card-body");

        const viewDiv = document.createElement("div");

        const title = document.createElement("h4");
        title.textContent = task.title;
        title.classList.add("card-title");

        const desc = document.createElement("p");
        desc.textContent = task.description;
        desc.classList.add("card-text");

        const status = document.createElement("p");
        status.textContent = `Status: ${statusLabel[task.status]}`;
        status.classList.add("card-text");

        const deadline = document.createElement("p");
        deadline.textContent = `Deadline: ${new Date(task.deadline).toLocaleString()}`;
        deadline.classList.add("card-text");

        const footerView = document.createElement("div");
        footerView.classList.add("d-flex", "justify-content-between");

        const editBtn = document.createElement("button");
        editBtn.textContent = "Edit";
        editBtn.classList.add("btn", "btn-sm", "btn-primary");

        const delBtn = document.createElement("button");
        delBtn.textContent = "Delete";
        delBtn.classList.add("btn", "btn-sm", "btn-danger");

        footerView.append(editBtn, delBtn)
        viewDiv.append(title, desc, status, deadline, footerView);


        //format tasks for EDITING in line
        const editDiv = document.createElement("div");
        editDiv.classList.add("d-none");

        const titleInput = document.createElement("input");
        titleInput.type = "text";
        titleInput.value = task.title;
        titleInput.classList.add("form-control", "mb-2");

        const descInput = document.createElement("textarea");
        descInput.value = task.description;
        descInput.classList.add("form-control", "mb-2");

        const statusInput = document.createElement("select");
        statusInput.className = "form-select";

        Object.keys(statusLabel).forEach(val => {
            const option = document.createElement("option");
            option.value = val;

            option.textContent = statusLabel[val];

            if (val == task.status) option.selected = true;
            statusInput.appendChild(option);
        });

        statusInput.classList.add("form-select", "mb-2");
        const deadlineInput = document.createElement("input");
        deadlineInput.type = "datetime-local";
        deadlineInput.value = task.deadline;
        deadlineInput.classList.add("form-control", "mb-2");
        deadlineInput.max = "9999-12-31T23:59";


        const footer = document.createElement("div");
        footer.classList.add("d-flex", "justify-content-between");

        const saveBtn = document.createElement("button");
        saveBtn.textContent = "Save";
        saveBtn.classList.add("btn", "btn-sm", "btn-primary");

        const cancelBtn = document.createElement("button");
        cancelBtn.textContent = "Cancel";
        cancelBtn.classList.add("btn", "btn-sm", "btn-secondary");

        footer.append(saveBtn, cancelBtn);
        editDiv.append(titleInput, descInput, statusInput, deadlineInput, footer);

        //button event listeners
        editBtn.addEventListener("click", () => {
            viewDiv.classList.add("d-none");
            editDiv.classList.remove("d-none");
        });

        delBtn.addEventListener("click", async () => {
            const conf = confirm(`Are you sure you want to delete "${task.title}"?`);

            if (conf) {
                try {
                    const res = await fetch(`${API_BASE_URL}/${task.id}`, {
                        method: "DELETE",
                        headers: {
                            "Authorization": "Bearer " + sessionStorage.getItem("jwtToken")
                        }
                    });

                    if (res.ok) {
                        card.remove();
                        successMessage.innerHTML = "Task deleted successfully";
                        successMessage.classList.remove("d-none");

                        setTimeout(() => successMessage.classList.add("d-none"), 3000);
                    } else {
                        throw new Error("Failed to delete!");
                    }
                } catch (err) {
                    alert("Error: " + err.message);
                }
            }
        });

        cancelBtn.addEventListener("click", () => {
            editDiv.classList.add("d-none");
            viewDiv.classList.remove("d-none");

            titleInput.value = task.title;
            descInput.value = task.description;
            statusInput.value = task.status;
            if (task.deadline) deadlineInput.value = task.deadline.slice(0, 16);
        });

        saveBtn.addEventListener("click", async () => {
            const updatedDeadline = deadlineInput.value;

            if (!task.id) {
                alert("Error: Task ID missing from server response.");
                return;
            }
            //pass updated task data to backend 
            const updatedTask = {
                title: titleInput.value,
                description: descInput.value,
                status: statusInput.value,
                deadline: updatedDeadline
            };

            try {
                const res = await fetch(`${API_BASE_URL}/${task.id}`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": "Bearer " + sessionStorage.getItem("jwtToken")
                    },
                    body: JSON.stringify(updatedTask)
                });

                if (!res.ok) throw new Error("Failed to update task");

                const index = tasksAll.findIndex(t => t.id === task.id);
                if (index !== -1) {
                    tasksAll[index] = { ...task, ...updatedTask };
                }
                //update view Div
                title.textContent = updatedTask.title;
                desc.textContent = updatedTask.description;
                status.textContent = `Status: ${statusLabel[updatedTask.status]}`;
                deadline.textContent = `Deadline: ${new Date(updatedTask.deadline).toLocaleString()}`;

                editDiv.classList.add("d-none");
                viewDiv.classList.remove("d-none");
            } catch (err) {
                alert("Error updating task: " + err.message);
            }
            applyFilter();
        });

        cardBody.append(viewDiv, editDiv);
        card.appendChild(cardBody);
        taskList.appendChild(card);
    });
}

// Filter tasks
function applyFilter() {
    let filtered = [...tasksAll];
    //search by task title 
    const searchTerm = filterSearch.value.trim().toLowerCase();
    if (searchTerm) {
        filtered = filtered.filter(task =>
            task.title.toLowerCase().includes(searchTerm)
        );
    }
    //search by status vaule 
    const statusVal = filterStatus.value;
    if (statusVal) {
        filtered = filtered.filter(task => task.status === statusVal);
    }
    //sort by deadline date 
    const sortVal = filterSort.value;
    if (sortVal === "deadline_asc") {
        filtered.sort((a, b) => (new Date(a.deadline).getTime() || 0) - (new Date(b.deadline).getTime() || 0));
    } else if (sortVal === "deadline_desc") {
        filtered.sort((a, b) => (new Date(b.deadline).getTime() || 0) - (new Date(a.deadline).getTime() || 0));
    }
    //render the filtered tasks 
    renderTasks(filtered);
};

//navigation update- if logged in, show username and logout. If logged out, show login and register
function updateNav() {
    const token = sessionStorage.getItem("jwtToken");
    const username = sessionStorage.getItem("username");
    const currentPage = window.location.pathname;

    const loggedInGroup = document.getElementById("logged-in-group");
    const loggedOutGroup = document.getElementById("logged-out-group");
    const userDisplay = document.getElementById("user-display");

    const isAuthPage = currentPage.includes("login") || currentPage.includes("register");

    if (!token) {
        if (loggedInGroup) loggedInGroup.classList.add("d-none");
        if (loggedOutGroup) loggedOutGroup.classList.remove("d-none");

        if (!isAuthPage) {
            window.location.href = "login.html";
        }
        return;   
    }
    else {
        if (loggedInGroup) loggedInGroup.classList.remove("d-none");
        if (loggedOutGroup) loggedOutGroup.classList.add("d-none");
        if (userDisplay) userDisplay.textContent = `${username || 'User'}`;
    }
};
