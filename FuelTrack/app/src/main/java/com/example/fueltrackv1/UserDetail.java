package com.example.fueltrackv1;

public class UserDetail {
    String firstName, lastName, password, mobile,sex,userStatus, address;

    public  UserDetail() {
        //Empty constructor
    }

    public UserDetail(String firstName, String lastName, String password, String mobile,
                       String sex, String userStatus, String address)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.mobile = mobile;
        this.sex = sex;
        this.userStatus = userStatus;
        this.address = address;
    }

    public String getWork() {
        return userStatus;
    }

    public void setWork(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getSex() { return sex; }

    public void setSex(String sex) { this.sex = sex; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) { this.password = password;  }

    public String getMobile() { return mobile; }

    public void setMobile(String mobile) { this.mobile = mobile; }

}
