package com.example.foodorder.Model;

import java.io.Serializable;

public class User {
    private String username;
    private String phone;
    private String email;
    private boolean isPhoneVerified;
    private boolean isStaff;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getIsPhoneVerified() {
        return isPhoneVerified;
    }

    public void setIsPhoneVerified(boolean isPhoneVerified) {
        this.isPhoneVerified = isPhoneVerified;
    }

    public boolean getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(boolean staff) {
        this.isStaff = staff;
    }

    public User(String username, String phone, String email, boolean isPhoneVerified) {
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.isPhoneVerified = isPhoneVerified;
        this.isStaff = false;
    }
}
