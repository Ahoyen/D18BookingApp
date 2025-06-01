package com.ptit.aird18bookingapp.models;

public class UserRegister {
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public String email;
    public String password;
    public String sex;
    public String birthday;
    public String avatarPath;

    public UserRegister(String firstName, String lastName, String phoneNumber, String email, String password, String sex, String birthday, String avatarPath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.sex = sex;
        this.birthday = birthday;
        this.avatarPath = avatarPath;
    }
}
