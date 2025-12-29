package fr.upjvthomashromain.bibliouniv.controller;

import fr.upjvthomashromain.bibliouniv.configuration.JwtUtil;
import fr.upjvthomashromain.bibliouniv.entity.User;
import fr.upjvthomashromain.bibliouniv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginRequest) {
        System.out.println("Step 1: Login request received");
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        System.out.println("Step 2: Extracted username: " + username);

        userRepository.findAll().forEach(u -> System.out.println("User exists: " + u.getUsername()));

        User user = userRepository.findByUsername(username);
        System.out.println("Step 3: User lookup result: " + (user != null ? "found" : "not found"));
        if (user == null) {
            System.out.println("Step 4: Returning 401 - Username not found");
            Map<String, String> error = new HashMap<>();
            error.put("message", "Username not found");
            return ResponseEntity.status(401).body(error);
        }

        System.out.println("Step 5: User found, proceeding to authentication");
        try {
            System.out.println("Step 6: Calling authentication manager");
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            System.out.println("Step 7: Authentication successful");

            String token = jwtUtil.generateToken(username);
            System.out.println("Step 8: Token generated successfully");
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            System.out.println("Step 9: Returning success response");
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            System.out.println("Step 7: BadCredentialsException - Invalid password");
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid password");
            return ResponseEntity.status(401).body(error);
        } catch (AuthenticationException e) {
            System.out.println("Step 7: AuthenticationException - " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Authentication failed: " + e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/auth/refresh")
    public Map<String, String> refresh(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            String username = jwtUtil.extractUsername(jwt);
            if (!jwtUtil.isTokenExpired(jwt)) {
                String newToken = jwtUtil.generateToken(username);
                Map<String, String> response = new HashMap<>();
                response.put("token", newToken);
                return response;
            }
        }
        throw new RuntimeException("Invalid token");
    }
}