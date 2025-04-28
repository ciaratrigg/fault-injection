package com.trigg.fault_injection.Utilities;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Scope("singleton")
public class ShellAuthContext {
    private String currentUsername;
    private String role;

    public void login(String username, String role) {
        this.currentUsername = username;
        this.role = role;
    }

    public void logout() {
        this.currentUsername = null;
        this.role = null;
    }

    public boolean isAuthenticated() {
        return currentUsername != null;
    }

    public boolean hasRole(String role) {
        return this.role.equalsIgnoreCase(role);
    }

    public String getRole(){ return this.role;}

    public String getUsername() {
        return currentUsername;
    }
}
