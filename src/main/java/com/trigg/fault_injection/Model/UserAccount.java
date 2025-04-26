package com.trigg.fault_injection.Model;

public class UserAccount {
    private int id;
    private String username;
    private String password;
    private boolean approved;

    public UserAccount(int id, String password, String username, boolean approved) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.approved = approved;
    }


    public UserAccount() {
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
