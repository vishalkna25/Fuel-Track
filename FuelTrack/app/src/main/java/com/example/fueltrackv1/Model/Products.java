package com.example.fueltrackv1.Model;

public class Products
{
    String FirstName, LastName, PhoneNo, image;
    public  Products()
    {

    }

    public Products(String firstName, String lastName, String phoneNo, String Image) {
        FirstName = firstName;
        LastName = lastName;
        PhoneNo = phoneNo;
        image = Image;
    }

    public String getProfile() {
        return image;
    }

    public void setProfile(String Image) {
        image = Image;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }
}
