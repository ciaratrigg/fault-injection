package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.UserAccount;
import com.trigg.fault_injection.Service.AppUserService;
import com.trigg.fault_injection.Utilities.AuthContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.Console;
import java.util.Scanner;

@ShellComponent
public class AuthCommands {
    private PasswordEncoder passwordEncoder;
    private AuthContext authContext;
    private AppUserService appUserService;

    @Autowired
    public AuthCommands(PasswordEncoder passwordEncoder, AuthContext authContext, AppUserService appUserService) {
        this.passwordEncoder = passwordEncoder;
        this.authContext = authContext;
        this.appUserService = appUserService;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @ShellMethod("Register a new user")
    public String register(@ShellOption String username, @ShellOption String password) {
        String encodedPassword = passwordEncoder.encode(password);

        Integer existing = appUserService.checkExistingUser(username);
        if (existing != null && existing > 0) {
            return "User already exists.";
        }

        appUserService.registerUserAccount(username, encodedPassword);

        return "User registered, awaiting approval from administrator.";
    }

    @ShellMethod("Login with your username (you will be prompted for your password)")
    public String login(@ShellOption String username) {
        if (authContext.isAuthenticated()) {
            return "Already logged in as " + authContext.getUsername();
        }

        String password;
        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword("Enter password: ");
            password = new String(passwordChars);
        } else {
            System.out.print("Enter password (WARNING: input will be visible): ");
            Scanner scanner = new Scanner(System.in);
            password = scanner.nextLine();
        }

        UserAccount account = appUserService.retrieveAccount(username);
        if (account == null) {
            return "User not found";
        }

        if (!passwordEncoder.matches(password, account.getPassword())) {
            return "Incorrect password, login unsuccessful.";
        }

        if (!account.isApproved()) {
            return "Account not approved. Please contact an admin.";
        }

        String userRole = appUserService.getUserRole(account.getId());
        authContext.login(username, userRole);
        return "Login successful. Welcome, " + username + "!";
    }

    @ShellMethod("Logout of the current session")
    public String logout() {
        if (authContext.isAuthenticated()) {
            authContext.logout();
            return "Logged out.";
        } else {
            return "Not currently logged in";
        }
    }
}
