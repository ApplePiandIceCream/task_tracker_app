package com.dts.app.service;

import com.dts.app.model.User;
import com.dts.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;


// required so that can use Spring Security User object- wraps user in spring security user so can use their auth. 
@Service
public class CustomUserDetailsService implements UserDetailsService {
// communicates with:
    @Autowired
    private UserRepository userRepository;

    //wrap user ID in UserDetails(Srping Secuirty)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
        .orElseThrow(()-> 
    new UsernameNotFoundException("User not found : " + username));
    
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}
