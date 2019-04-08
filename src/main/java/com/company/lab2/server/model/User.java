package com.company.lab2.server.model;

public class User {
    private String login;
    private String password;
    private boolean banned;
    private String admin;

    public User(String login, String password, boolean isBanned, String admin) {
        this.login = login;
        this.password = password;
        this.banned = isBanned;
        this.admin = admin;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean isBanned() {
        return banned;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }
}
