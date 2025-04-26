package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.UserAccount;
import com.trigg.fault_injection.Service.AppUserService;
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
    private JdbcTemplate jdbcTemplate;
    private ShellAuthContext authContext;
    private AppUserService appUserService;

    @Autowired
    public AdminCommands(JdbcTemplate jdbcTemplate, ShellAuthContext authContext, AppUserService appUserService) {
        this.jdbcTemplate = jdbcTemplate;
        this.authContext = authContext;
        this.appUserService = appUserService;
    }

    @ShellMethod("Upgrade user permissions")
    public String upgradeUser(@ShellOption String username) {
        if (!checkAdmin()) {
            return "Only admins can assign roles.";
        }

        UserAccount account = appUserService.retrieveAccount(username);
        if(account == null){
            return "User " + username + " not found.";
        }
        else{
            String currentRole = appUserService.getUserRole(account.getId());
            if(currentRole.equalsIgnoreCase("ROLE_ADMIN")){
                return "User " + username + " is already an admin.";
            }
            else{
                appUserService.upgradeUserRole(account.getId());
            }
        }
        return "Assigned ROLE_ADMIN to " + username;
    }

    @ShellMethod("Downgrade user permissions")
    public String downgradeUser(@ShellOption String username){
        if (!checkAdmin()) {
            return "Only admins can assign roles.";
        }

        UserAccount account = appUserService.retrieveAccount(username);
        if(account == null){
            return "User " + username + " not found.";
        }
        else{
            String currentRole = appUserService.getUserRole(account.getId());
            if(currentRole.equalsIgnoreCase("ROLE_USER")){
                return "User " + username + " is already a user.";
            }
            else{
                appUserService.downgradeUserRole(account.getId());
            }
        }
        return "Assigned ROLE_USER to " + username;
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
