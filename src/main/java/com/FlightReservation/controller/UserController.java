package com.FlightReservation.controller;

import com.FlightReservation.entities.Role;
import com.FlightReservation.entities.User;
import com.FlightReservation.payload.SigninDto;
import com.FlightReservation.payload.UserDTO;
import com.FlightReservation.repository.RoleRepository;
import com.FlightReservation.repository.UserRepository;
import com.FlightReservation.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    private AuthenticationManager authenticationManager;

    PasswordEncoder passwordEncoder;


    private CustomUserDetailsService userDetailsService;

    private final RoleRepository roleRepository;

    @Autowired
    public UserController(UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder, CustomUserDetailsService userDetailsService,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        // Check if the username or email already exists
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        // Validate the secret key
        if (userDTO.getSecretKey() != null && userDTO.getSecretKey().equals("ADMIN_ONLY")) {
            // Create a new User entity with ADMIN role
            User user = new User();
            user.setFullName(userDTO.getFullName());
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            // Set other properties as needed

            Role adminRole = roleRepository.findByName("ROLE_ADMIN"); // Assuming the role name is "ADMIN"
            user.getRoles().add(adminRole);

            // Save the user
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body("Admin user registered successfully");
        } else {
            // Create a new User entity with USER role
            User user = new User();
            user.setFullName(userDTO.getFullName());
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            // Set other properties as needed

            Role userRole = roleRepository.findByName("ROLE_USER"); // Assuming the role name is "USER"
            user.getRoles().add(userRole);

            // Save the user
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body("Regular user registered successfully");
        }
    }
    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@RequestBody SigninDto
                                                           signinDto){
        Authentication authentication = authenticationManager.authenticate(
                new
                        UsernamePasswordAuthenticationToken(signinDto.getUsername(),
                        signinDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>("User signed-in successfully!.",
                HttpStatus.OK);
    }
}

