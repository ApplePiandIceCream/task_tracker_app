// URL variable 
let API_BASE_URL;

if (window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1" || window.location.protocol === "file:") {
    API_BASE_URL = "http://localhost:8080/api/tasks";
} else {
    API_BASE_URL = "https://task-tracker-app-w02n.onrender.com/api/tasks"; 
}

const taskList = document.getElementById("task-list");
const form = document.getElementById("taskForm");
const successMessage = document.getElementById("successMessage");
const errorMessage = document.getElementById("errorMessage");

const filterSearch = document.getElementById("filterSearch");
const filterSort = document.getElementById("filterSort");
const filterStatus = document.getElementById("filterStatus");
const filterClear = document.getElementById("filterClear");

const statusLabel = {"PENDING": "Pending",
        "IN_PROGRESS": "In Progress",
        "AWAITING_RESPONSE": "Awaiting Response",
        "COMPLETED": "Completed"};


let editingTaskId = null;
let tasksAll = [];

//prevent submission of past date  
window.addEventListener("DOMContentLoaded", () => {
    // Get HTML input element for deadline
    const DeadlineInput = document.getElementById("deadline");

    //Get current date/ time
    const now = new Date();
    //convert current time to YY-MM-DDTHH:MM format
    const localISOTime = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0,16);

    //min value for date picker
    DeadlineInput.min=localISOTime;


    //call load tasks API
    loadTasks();
});


//Form submission event listener
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


//Load tasks in list card
async function loadTasks() {
    const taskContainer = document.getElementById('task-list');
    if (taskContainer) {    
        taskContainer.innerHTML = '<p class = "loading-text">Connecting to server... (waking up cloud host- please wait)</p>';
    }
    try {
        const res = await fetch(API_BASE_URL);
        
        if (!res.ok) {
            throw new Error("Server is still warming up...");
        }

        const tasks = await res.json();
        tasksAll = tasks;
        renderTasks(tasks);

    } catch (err) {
        console.log("Backend is still sleeping, retrying in 5 seconds...");
        
        setTimeout(loadTasks, 5000);
    }
}


function renderTasks(tasks) {
    taskList.innerHTML='';

    

    if (tasks.length == 0) {
        taskList.textContent = "No tasks listed";
        return
    }

    tasks.forEach(task=> {
        //view each task
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
        

        //edit task inline
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
        cancelBtn.classList.add("btn", "btn-sm", "btn-secondary" );

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
                method: "DELETE"
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
                    alert("Error: " + err.message) ;
                }
        }
    });

        cancelBtn.addEventListener("click", () => {
            editDiv.classList.add("d-none");
            viewDiv.classList.remove("d-none");

            titleInput.value = task.title;
            descInput.value = task.description;
            statusInput.value = task.status;
            if (task.deadline) deadlineInput.value = task.deadline.slice(0,16);
        });

        saveBtn.addEventListener("click", async ()=> {
            const updatedDeadline = deadlineInput.value;

            if (!task.id) {
                alert("Error: Task ID missing from server response.");
                return;

            }

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
                        "Content-Type": "application/json"
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

    const searchTerm = filterSearch.value.trim().toLowerCase();
    if (searchTerm) {
        filtered = filtered.filter(task =>
            task.title.toLowerCase().includes(searchTerm)
        );
    }
    const statusVal = filterStatus.value;
    if (statusVal) {
        filtered = filtered.filter(task => task.status === statusVal);
    }

    const sortVal = filterSort.value;
    if (sortVal === "deadline_asc") {
        filtered.sort((a,b) => (new Date(a.deadline).getTime() || 0) - (new Date(b.deadline).getTime() || 0));
    } else if (sortVal === "deadline_desc") {
        filtered.sort((a,b) => (new Date(b.deadline).getTime() || 0) - (new Date(a.deadline).getTime() || 0));
    }


    renderTasks(filtered);
}


    filterSearch.addEventListener("input", applyFilter);
    filterSort.addEventListener("change", applyFilter);
    filterStatus.addEventListener("change", applyFilter);
    filterClear.addEventListener("click", () => {
    filterSearch.value = "";
    filterSort.value = "";
    filterStatus.value = "";
    renderTasks(tasksAll);
});


