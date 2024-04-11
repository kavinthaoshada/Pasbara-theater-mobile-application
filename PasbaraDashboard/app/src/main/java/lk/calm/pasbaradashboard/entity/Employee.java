package lk.calm.pasbaradashboard.entity;

public class Employee {
    private String userName;
    private String email;
    private String contact;
    private String password;
    private boolean isEmailVerified;

    public Employee() {
    }

    public Employee(String userName, String email, String contact, String password, boolean isEmailVerified) {
        this.userName = userName;
        this.email = email;
        this.contact = contact;
        this.password = password;
        this.isEmailVerified = isEmailVerified;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }
}
