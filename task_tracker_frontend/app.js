// URL variable 
const API_BASE_URL = "http://localhost:8080/api/tasks"

const taskList = document.getElementById("task-list");
const form = document.getElementById("taskForm");
const successMessage = document.getElementById("successMessage");
const errorMessage = document.getElementById("errorMessage");

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
        const res = await fetch(API_BASE_URL, {
            method: "POST",
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
        form.reset();

        //catch network/ parsing errors 
    } catch (error) {
        console.error("Fetch or processing error:", error);
        errorMessage.innerText = "Something went wrong. Try again.";
        errorMessage.classList.remove("d-none");
    }
});

async function loadTasks() {
    const res = await fetch(API_BASE_URL) //MUST UPDATE THIS MUST UPDATE THIS MUST UPDATE THIS FOR DEPLOYMENT!!!!
    const tasks = await res.json();


    taskList.innerHTML='';

    if (tasks.length == 0) {
        taskList.textContent = "No tasks listed";
        return
    }

    tasks.forEach(task=> {
        const li = document.createElement("li");
        li.classList.add("task-list", "mb-3", "p-3", "border", "rounded");

        const title = document.createElement("h4");
        title.textContent = task.title;

        const desc = document.createElement("p");
        desc.textContent = task.description;

        const status = document.createElement("p");
        status.textContent = `Status: ${task.status}`;

        const deadline = document.createElement("p"); 
        deadline.textContent = `Deadline: ${new Date(task.deadline).toLocaleString()}`;

        li.append(title, desc, status, deadline);
        taskList.appendChild(li);

    });

}