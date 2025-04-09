package com.jaixlabs.securepay.model;


public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String u, String p) {
        username = u;
        password = p;
    }

    // Optional: Add getters if needed
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
