package fr.upjvthomashromain.bibliouniv.controller;


import fr.upjvthomashromain.bibliouniv.configuration.StartupDatabaseInitializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
// This ensures that ONLY users with the 'admin' role can even enter this controller
@PreAuthorize("hasAuthority('admin')") 
public class AdminController {

    @Autowired
    private StartupDatabaseInitializer initializer;

    @GetMapping("/reset-db") // Better to use PostMapping for state-changing actions
    public String resetDatabase() {
        // No need for manual isAdmin check anymore due to @PreAuthorize
        try {
            initializer.resetDatabase();
            return "Database reset successfully";
        } catch (Exception e) {
            return "Error resetting database: " + e.getMessage();
        }
    }

    @GetMapping("/test")
    public String test() {
        return "ok";
    }
}