package com.ptit.aird18bookingapp.models;

public class User{
    public int id;
    public boolean status;
    public long createdDate;
    public long updatedDate;
    public String firstName;
    public String lastName;
    public String sex;
    public String birthday;
    private String email;
    public String cookie;
    public Role role;
    public String phoneNumber;
    public boolean phoneVerified;
    public Object about;
    private String fullName;
    private String avatarPath;
    public String fullPathAddress;
//    public AddressDetails addressDetails;
    public Object addressDetails;
    public boolean supremeHost;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getName() {
        return fullName;
    }
    public void setName(String fullName) {
        this.fullName = fullName;
    }
    public String getAvatarPath() {
        return avatarPath;
    }
    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}

