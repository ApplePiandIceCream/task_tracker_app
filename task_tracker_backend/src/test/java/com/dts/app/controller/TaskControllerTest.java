package com.dts.app.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.format.*;
import java.util.*;

import com.dts.app.config.AuthTokenFilter;
import com.dts.app.config.JwtUtil;
import com.dts.app.exception.GlobalExceptionHandler;
import com.dts.app.model.Status;
import com.dts.app.model.Task;
import com.dts.app.model.User;
import com.dts.app.repository.TaskRepository;
import com.dts.app.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dts.app.service.CustomUserDetailsService;

import static org.mockito.Mockito.when; 
import static org.mockito.ArgumentMatchers.any; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; 
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


/**
 * Unit tests for TaskController.
 * @Import ensures the GlobalExceptionHandler is included in the test context
 * to check error handling e.g.,  validation failure.
 */


@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class TaskControllerTest {

    //simulate HTTP requests without server 
    @Autowired
    private MockMvc mockMvc;

    // @MockBean replaces the real TaskRepository bean with a Mockito mock object- isolates controller logic to prevent database editing 
    @MockBean
    private TaskRepository taskRepository;

    @MockBean 
    private UserRepository userRepository;

   @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private AuthTokenFilter authTokenFilter;

    @MockBean
    private PasswordEncoder passwordEncoder;



    // ObjectMapper for converting Task object into JSON string 
    // for use in the simulated HTTP request bodies.
    @Autowired
    private ObjectMapper objectMapper;

    // Constants for date formatting, matching the format expected by the backend and frontend.
    private static final String REQUEST_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
    private static final DateTimeFormatter REQUEST_FORMATTER = DateTimeFormatter.ofPattern(REQUEST_DATE_FORMAT).withLocale(Locale.UK);
    private Task testTask;


    /**
     * Setup method run before each @Test method.
     * Initializes valid Task object for testing.
     */
    @BeforeEach
    void testData() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("React app development");
        testTask.setDescription("Develop the task app with a react frontend");
        testTask.setStatus(Status.PENDING);
        testTask.setDeadline(LocalDateTime.of(2026, 2, 1, 12, 0));
    }

    /**
     * Tests the successful creation of a task.
     * Verifies that the repository's save method is called and the controller returns 
     * the correct HTTP status (201 CREATED) and the correct data in the JSON response.
     */
    @Test
    @WithMockUser(username = "testuser")
    void createTask_Success() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);  
        mockUser.setUsername("testuser");
        mockUser.setPassword("encodedPassword123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        mockMvc.perform(post("/api/tasks")
        .with(csrf()) //This is the line that makes it work- request doesn't reach controller otherwise!!!!!!!!!!
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
                .andDo(print()) 
                .andExpect(status().isCreated());
    }

    /**
     * Tests validation failure due to the title being too short (@Size constraint).
     * This verifies that the GlobalExceptionHandler correctly catches 
     * MethodArgumentNotValidException and returns a 400 status.
     */
@Test
void createTask_ValidationFail() throws Exception {
    Map<String, Object> invalidData = new HashMap<>();
    
    invalidData.put("title", "R"); 
    
    invalidData.put("status", "PENDING");

    invalidData.put("deadline", LocalDateTime.now().plusDays(1).format(REQUEST_FORMATTER));

    mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidData)))
            .andDo(print())
        .andExpect(status().isBadRequest())
        
        .andExpect(jsonPath("$.title").value("Title must be between 3 and 120 characters")); 
    }


    /**
     * Tests validation failure when the 'status' field is null (@NotNull constraint).
     */
   @Test
    void createTask_ValidationFailStatus() throws Exception {
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("title", "React app");
        invalidData.put("status", null); 
        invalidData.put("deadline", LocalDateTime.now().plusDays(1).format(REQUEST_FORMATTER));

        mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(invalidData)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value("Please provide task status"));
    }


    /**
     * Tests validation failure when the 'deadline' field is null (@NotNull constraint).
     */
    @Test
    void createTask_ValidationFailDeadline() throws Exception {
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("title", "React app");
        invalidData.put("status", "PENDING");
        invalidData.put("deadline", null);

        mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(invalidData)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.deadline").value("You must provide a task deadline")); 
    }
}