package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.UserAccount;
import com.trigg.fault_injection.Service.AppUserService;
import com.trigg.fault_injection.Utilities.ShellAuthContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final AppUserService appUserService;
    private final ShellAuthContext authContext;

    @Autowired
    public AuthController(PasswordEncoder passwordEncoder, AppUserService appUserService, ShellAuthContext authContext) {
        this.passwordEncoder = passwordEncoder;
        this.appUserService = appUserService;
        this.authContext = authContext;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String rawPassword = request.get("password");

        if (username == null || rawPassword == null) {
            return ResponseEntity.badRequest().body("Username and password must be provided.");
        }

        Integer existing = appUserService.checkExistingUser(username);
        if (existing != null && existing > 0) {
            return ResponseEntity.badRequest().body("User already exists.");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        appUserService.registerUserAccount(username, encodedPassword);

        return ResponseEntity.ok("User registered, awaiting approval from administrator.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password are required.");
        }

        if (authContext.isAuthenticated()) {
            return ResponseEntity.ok("Already logged in as " + authContext.getUsername());
        }

        UserAccount account = appUserService.retrieveAccount(username);
        if (account == null) {
            return ResponseEntity.status(401).body("User not found.");
        }

        if (!passwordEncoder.matches(password, account.getPassword())) {
            return ResponseEntity.status(401).body("Incorrect password, login unsuccessful.");
        }

        if (!account.isApproved()) {
            return ResponseEntity.status(403).body("Account not approved. Please contact an admin.");
        }

        String userRole = appUserService.getUserRole(account.getId());
        authContext.login(username, userRole);

        return ResponseEntity.ok("Login successful. Welcome, " + username + "!");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        if (authContext.isAuthenticated()) {
            authContext.logout();
            return ResponseEntity.ok("Logged out.");
        } else {
            return ResponseEntity.ok("Not currently logged in.");
        }
    }
}
