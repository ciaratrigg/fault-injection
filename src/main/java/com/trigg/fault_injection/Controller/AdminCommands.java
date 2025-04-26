package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Utilities.ShellAuthContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
public class AdminCommands {
    private final JdbcTemplate jdbcTemplate;
    private final ShellAuthContext authContext;

    @Autowired
    public AdminCommands(JdbcTemplate jdbcTemplate, ShellAuthContext authContext) {
        this.jdbcTemplate = jdbcTemplate;
        this.authContext = authContext;
    }

    @ShellMethod("Assign a role to a user")
    public String assignRole(@ShellOption String username, @ShellOption String role) {
        if (!checkAdmin()) {
            return "Only admins can assign roles.";
        }

        Integer uid;
        try {
            uid = jdbcTemplate.queryForObject(
                    "SELECT u_id FROM user_account WHERE username = ?",
                    Integer.class,
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return "User not found: " + username;
        }

        // Normalize the role (optional, but helps reduce errors)
        String normalizedRole = role.toUpperCase();
        if (!normalizedRole.startsWith("ROLE_")) {
            normalizedRole = "ROLE_" + normalizedRole;
        }

        int count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM authority WHERE u_id = ? AND role = ?",
                Integer.class,
                uid, normalizedRole
        );

        if (count > 0) {
            return "User already has role " + normalizedRole;
        }

        jdbcTemplate.update(
                "INSERT INTO authority (u_id, role) VALUES (?, ?)",
                uid, normalizedRole
        );

        return "Assigned role " + normalizedRole + " to " + username;
    }
    @ShellMethod("Users awaiting approval")
    public String pendingUsers() {
        if (!checkAdmin()) {
            return "Only admins can view pending users.";
        }

        List<String> pendingUsers = jdbcTemplate.query(
                "SELECT username FROM user_account WHERE approved = FALSE",
                (rs, rowNum) -> rs.getString("username")
        );

        if (pendingUsers.isEmpty()) {
            return "No users awaiting approval.";
        }

        StringBuilder result = new StringBuilder("Users awaiting approval:\n");
        for (String user : pendingUsers) {
            result.append("- ").append(user).append("\n");
        }

        return result.toString();
    }

    @ShellMethod("Approve a user registration")
    public String approveUser(@ShellOption String username) {
        if (!checkAdmin()) {
            return "Only admins can approve users.";
        }

        Integer uid;
        try {
            uid = jdbcTemplate.queryForObject(
                    "SELECT u_id FROM user_account WHERE username = ?",
                    Integer.class,
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return "User not found: " + username;
        }

        jdbcTemplate.update("UPDATE user_account SET approved = true WHERE u_id = ?", uid);

        return "User " + username + " approved successfully.";
    }

    public boolean checkAdmin(){
        return authContext.isAuthenticated() && authContext.hasRole("ROLE_ADMIN");
    }

}
