package com.company.lab2.server.model;

/**
 * client description class
 */
public class User {
    /** client login */
    private String login;
    /** client password */
    private String password;
    /** client ban status */
    private boolean banned;
    /** client admin status */
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

    /**
     * a method that returns the value field of a given object.
     *@return client information
     */
    @Override
    public String toString() {
        return "Login: '" + login +
                "', password: '" + password +
                "', banned: " + banned +
                ", admin: '" + admin + '\'';
    }

    /**
     * method for checking customer equality
     * @param o - the object with which this one is compared
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (isBanned() != user.isBanned()) return false;
        if (!getLogin().equals(user.getLogin())) return false;
        if (!getPassword().equals(user.getPassword())) return false;
        return getAdmin().equals(user.getAdmin());
    }

    /**
     * method returning the hash user code
     *@return the value of the hash code
     */
    @Override
    public int hashCode() {
        int result = getLogin().hashCode();
        result = 31 * result + getPassword().hashCode();
        result = 31 * result + (isBanned() ? 1 : 0);
        result = 31 * result + getAdmin().hashCode();
        return result;
    }
}
