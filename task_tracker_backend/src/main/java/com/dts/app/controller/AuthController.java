package com.dts.app.controller;

import com.dts.app.model.User;
import com.dts.app.repository.UserRepository;
import com.dts.app.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Handles Authentication request (Login/ register)
 * @CrossOrigin allows frontend to talk to this backend 
 */
@CrossOrigin(origins = "https://applepiandicecream.github.io", allowCredentials = "true") 
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtil jwtUtils;

    /**
     * Authenticate user and return JWT token 
     * @param user - contains username and plain- text password from login form
     * @return String containing JWT token if successful 
     */
    @PostMapping("/signin")
    public String authenticateUser(@RequestBody User user) {
        //verify credentials against database
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
    );
    //if valid, retrieve user details and generate JWT signed token 
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return jwtUtils.generateToken(userDetails.getUsername());
    }

    /**
     * Create new user account 
     * @param user- contains new user details from signup form 
     * @return Success message and redirect to login, or error if username exists. 
     */
    @PostMapping("/signup")
    public String registerUser(@RequestBody User user) {
        //prevent duplicate accounts 
        if (userRepository.existsByUsername(user.getUsername())) {
            return "Username is already taken- choose another!";
        }
        //Hash password before saving 
        User newUser = new User(
                user.getUsername(),
                encoder.encode(user.getPassword())
        );
        
        userRepository.save(newUser);
        return "User registered successfully!";
    }
    
}
