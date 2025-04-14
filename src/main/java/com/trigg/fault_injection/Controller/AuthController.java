package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Database.UserDAO;
import com.trigg.fault_injection.Model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserDAO userDAO;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserDAO userDAO, PasswordEncoder passwordEncoder){
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userDto) {
        String username = userDto.get("username");
        String password = passwordEncoder.encode(userDto.get("password"));

        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword(password);

        userDAO.createAccount(user, List.of("ROLE_USER")); // default role

        return ResponseEntity.ok("User registered.");
    }
}
