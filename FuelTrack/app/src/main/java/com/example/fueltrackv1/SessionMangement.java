package com.example.fueltrackv1;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.firestore.auth.User;

public class SessionMangement
{
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String Shared_Pref_Name = "session";
    String session_Key = "session_user";
    String username = "user_number";
    public SessionMangement(Context context)
    {
        sharedPreferences = context.getSharedPreferences(Shared_Pref_Name,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public int getSession()
    {
        return sharedPreferences.getInt(session_Key, -1);
    }

    public void saveSession(com.example.fueltrackv1.User user)
    {
        int id = user.getId();
        String name = user.getName();
        editor.putInt(session_Key,id).commit();
        editor.putString(username, name).commit();
    }

    public void removeSession()
    {
        editor.putInt(session_Key,-1).commit();
        editor.putString(username,"user_number").commit();
    }

    public String getUserName()
    {
        return sharedPreferences.getString(username, "user");
    }
}
