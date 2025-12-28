package fr.upjvthomashromain.bibliouniv.controller;

import fr.upjvthomashromain.bibliouniv.configuration.StartupDatabaseInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private StartupDatabaseInitializer initializer;

    @RequestMapping(value = "/reset-db", method = {RequestMethod.GET, RequestMethod.POST})
    public String resetDatabase() {
        System.out.println("Step 1: Received reset-db request");
        try {
            System.out.println("Step 2: Calling initializer.resetDatabase()");
            initializer.resetDatabase();
            System.out.println("Step 3: Database reset completed successfully");
            return "Database reset successfully";
        } catch (Exception e) {
            System.out.println("Step 3: Error during reset: " + e.getMessage());
            return "Error resetting database: " + e.getMessage();
        }
    }

    @GetMapping("/test")
    public String test() {
        return "ok";
    }
}