package com.example.dinetime.ui.home;

import java.util.ArrayList;

public class UserData {

    public String firstName, lastName, address;
    public ArrayList<String> allergies;
    public boolean isAdmin;

    public UserData(String firstName, String lastName, String address,
                    ArrayList<String> allergies, boolean isAdmin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.allergies = allergies;
        this.isAdmin = isAdmin;
    }

}
