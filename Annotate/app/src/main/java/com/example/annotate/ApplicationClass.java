package com.example.annotate;

import android.app.Application;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.List;

public class ApplicationClass  extends Application {
    public static final String APPLICATION_ID = "15FCC390-0410-769F-FFC5-29943BCDAA00";
    public static final String API_KEY = "ACB9A236-FA37-4403-B6C8-722561D75A93";
    public static final String SERVER_URL = "https://api.backendless.com";
    public static BackendlessUser user;
    public static List<Projects> projects;
    public static List<UserScore> userData;
    @Override
    public void onCreate(){
        super.onCreate();
        Backendless.setUrl( SERVER_URL );
        Backendless.initApp( getApplicationContext(),
                APPLICATION_ID,
                API_KEY );
    }
}
