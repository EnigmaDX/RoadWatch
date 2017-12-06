package com.example.derrickdowuona.roadwatch;

/**
 * Created by derrick.dowuona on 12/5/2017.
 */

public class Users {

    private  String name;
    private String email;
    private int phoneNum;
    private int age;

    public Users(String uname, String em, int phone, int agee)
    {
        name = uname;
        email = em;
        phoneNum = phone;
        age = agee;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public int getPhoneNum()
    {
        return phoneNum;
    }

    public int getAge()
    {
        return age;
    }

    public String toString()
    {

        return "Name: " + getName() + "|| Email: " + getEmail() + "|| Phone: " + getPhoneNum()+ " ||" + "Age: " + getAge();
    }


}
