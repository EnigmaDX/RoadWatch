package com.example.derrickdowuona.roadwatch;

import android.net.Uri;

/**
 * Created by derrick.dowuona on 12/7/2017.
 */

public class Report {

    private String uri;
    private String description;
    private String username;

    public Report(String urii, String desc, String uname)
    {
        uri = urii;
        description = desc;
        username = uname;
    }

    public String getUsername()
    {
        return username;
    }

    public String getUri()
    {
        return uri;
    }

    public String getDescription()
    {
        return description;
    }

    public String toString()
    {
        return "Uri::: " + uri + "___Descr::: " + description + "___Username:::: " + username + "____";
    }




}
