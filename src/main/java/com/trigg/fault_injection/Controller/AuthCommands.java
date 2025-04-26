package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.UserAccount;
import com.trigg.fault_injection.Service.AppUserService;
import com.trigg.fault_injection.Utilities.ShellAuthContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
public class AuthCommands {
    private final PasswordEncoder passwordEncoder;
    private final ShellAuthContext authContext;
    private final AppUserService appUserService;

    @Autowired
    public AuthCommands(PasswordEncoder passwordEncoder,
                        ShellAuthContext authContext, AppUserService appUserService) {
        this.passwordEncoder = passwordEncoder;
        this.authContext = authContext;
        this.appUserService = appUserService;
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


    @ShellMethod("Login with your username and password")
    public String login(@ShellOption String username, @ShellOption String password) {
        UserAccount account = appUserService.retrieveAccount(username);
        if(account == null){
            return "User not found";
        }
        else{
            if (!passwordEncoder.matches(password, account.getPassword())) {
                return "Incorrect password, login unsuccessful";
            }

            if (!account.isApproved()) {
                return "Account not approved. Please contact an admin.";
            }

            List<String> userRoles = appUserService.getUserRoles(account.getId());

            authContext.login(username, userRoles);
            return "Login successful. Welcome, " + username + "!";
        }
    }


    @ShellMethod("Logout of the current session")
    public String logout() {
        authContext.logout();
        return "Logged out.";
    }
}
