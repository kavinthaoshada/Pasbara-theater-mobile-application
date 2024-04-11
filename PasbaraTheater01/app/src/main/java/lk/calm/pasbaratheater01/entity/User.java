package lk.calm.pasbaratheater01.entity;

public class User {
    private String name;
    private String password;
    private String email;
    private String contact_number;
    private boolean isEmailVerified;

    public User() {
    }

    public User(String name, String password, String email, String contact_number, boolean isEmailVerified) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.contact_number = contact_number;
        this.isEmailVerified = isEmailVerified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }
}
