package com.dts.app.controller;

import com.dts.app.config.JwtUtil;
import com.dts.app.repository.TaskRepository;
import com.dts.app.repository.UserRepository;
import com.dts.app.service.CustomUserDetailsService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
   @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtils;

    @Test
    @WithMockUser //bypass security filters so can test 
    void registerUser_Success() throws Exception {
        //tell fake repo to treat new user altready exists as false
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        // tell fake encoder t return 'hashed_password' instead of dong encryption 
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");
        //send fake HTTP POST to api 
        mockMvc.perform(post("/api/auth/signup")
                .with(csrf())// add security token to request- WITHOUT THIS, Spring Security blocks it. 
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"newuser\", \"password\":\"password123\"}"))
                .andExpect(status().isOk()) //check status matches
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    @WithMockUser
    void registerUser_Fail_UsernameExists() throws Exception {
        //tell fake repo to treat new user already exists as true
        when(userRepository.existsByUsername("bossman")).thenReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"bossman\", \"password\":\"password123\"}"))
                .andExpect(status().isOk())//check conflict is picked up on 
                .andExpect(content().string("Username is already taken- choose another!"));
    }
}
