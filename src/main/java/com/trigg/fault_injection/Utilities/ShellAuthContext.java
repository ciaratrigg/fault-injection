package com.trigg.fault_injection.Utilities;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Scope("singleton")
public class ShellAuthContext {
    private String currentUsername;
    private List<String> roles;

    public void login(String username, List<String> roles) {
        this.currentUsername = username;
        this.roles = roles;
    }

    public void logout() {
        this.currentUsername = null;
        this.roles = null;
    }

    public boolean isAuthenticated() {
        return currentUsername != null;
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public String getUsername() {
        return currentUsername;
    }
}
