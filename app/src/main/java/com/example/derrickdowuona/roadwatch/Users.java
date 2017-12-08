package com.example.derrickdowuona.roadwatch;

/**
 * Created by derrick.dowuona on 12/5/2017.
 */

public class Users {

    private  String name;
    private String email;
    private int phoneNum;
    private int age;
    private  String organisation;

    public Users(String uname, String em, int phone, int agee, String org)
    {
        name = uname;
        email = em;
        phoneNum = phone;
        age = agee;
        organisation = org;
    }

    public String getName()
    {
        return name;
    }

    private String getEmail()
    {
        return email;
    }

    private int getPhoneNum()
    {
        return phoneNum;
    }

    private int getAge()
    {
        return age;
    }

    private String getOrg()
    {
        return organisation;
    }

    public String toString()
    {

        return "Name: " + getName() + "|| Email: " + getEmail() + "|| Phone: " + getPhoneNum()+ " ||" + "Age: " + getAge() + "Organisation: " + getOrg();
    }


}
