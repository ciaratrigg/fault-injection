package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Utilities.ShellAuthContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Map;

@ShellComponent
public class AuthCommands {
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final ShellAuthContext authContext;

    @Autowired
    public AuthCommands(JdbcTemplate jdbcTemplate,
                        PasswordEncoder passwordEncoder,
                        ShellAuthContext authContext) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.authContext = authContext;
    }

    @ShellMethod("Register a new user")
    public String register(@ShellOption String username, @ShellOption String password) {
        String encodedPassword = passwordEncoder.encode(password);

        Integer existing = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_account WHERE username = ?",
                Integer.class, username);

        if (existing != null && existing > 0) {
            return "User already exists.";
        }

        jdbcTemplate.update(
                "INSERT INTO user_account (username, password) VALUES (?, ?)",
                username, encodedPassword);

        Integer uid = jdbcTemplate.queryForObject(
                "SELECT u_id FROM user_account WHERE username = ?",
                Integer.class, username);

        jdbcTemplate.update(
                "INSERT INTO authority (u_id, role) VALUES (?, ?)",
                uid, "ROLE_USER");

        return "User registered.";
    }

    @ShellMethod("Login with your username and password")
    public String login(@ShellOption String username, @ShellOption String password) {
        try {
            Map<String, Object> user = jdbcTemplate.queryForMap(
                    "SELECT u_id, password FROM user_account WHERE username = ?",
                    username);

            String storedPassword = (String) user.get("password");
            if (!passwordEncoder.matches(password, storedPassword)) {
                return "Incorrect password.";
            }

            Integer uid = (Integer) user.get("u_id");
            List<String> roles = jdbcTemplate.query(
                    "SELECT role FROM authority WHERE u_id = ?",
                    new Object[]{uid},
                    (rs, rowNum) -> rs.getString("role"));

            authContext.login(username, roles);
            return "Login successful. Welcome, " + username + "!";
        } catch (EmptyResultDataAccessException e) {
            return "User not found.";
        }
    }

    @ShellMethod("Logout of the current session")
    public String logout() {
        authContext.logout();
        return "Logged out.";
    }
}
