package com.proGrad.temporaryjobapp;


import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String SHARED_PREF_NAME = "temps";
    private static final String KEY_ID           = "keyid";
    private static final String KEY_LOGIN        = "keylogin";
    private static final String KEY_TYPE         = "keytype";
    private static final String KEY_NAME         = "keyname";
    private static final String KEY_PASS         = "keypass";
    private static final String KEY_FNAM         = "keyfname";
    private static final String KEY_EMAI         = "keyemail";
    private static final String KEY_PHON         = "keyphone";

    private static SessionManager mInstance;
    private static Context mCtx;

    private SessionManager(Context context) {
        mCtx = context;
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SessionManager(context);
        }
        return mInstance;
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_LOGIN, false);
    }


    public int getUserID() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ID, -1);
    }

    public void setUserID(int id) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor mEditor = sharedPreferences.edit();

        mEditor.putInt(KEY_ID, id);
        mEditor.commit();
    }

    public int getUserType() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_TYPE, -1);
    }

    public void setUserType(int type) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor mEditor = sharedPreferences.edit();

        mEditor.putInt(KEY_TYPE, type);
        mEditor.commit();
    }




    public void setLoggedIn(boolean loggedIn) {
        SharedPreferences sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPreferences.edit();

        mEditor.putBoolean(KEY_LOGIN, loggedIn);
        mEditor.commit();
    }

    public String getUserName() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NAME, "");
    }

    public void setUserName(String username) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor mEditor = sharedPreferences.edit();

        mEditor.putString(KEY_NAME, username);
        mEditor.commit();
    }

    public String getUserPass() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PASS, "");
    }

    public void setUserPass(String pass) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor mEditor = sharedPreferences.edit();

        mEditor.putString(KEY_PASS, pass);
        mEditor.commit();
    }

    public String getUserFname() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_FNAM, "");
    }

    public void setUserFname(String fname) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor mEditor = sharedPreferences.edit();

        mEditor.putString(KEY_FNAM, fname);
        mEditor.commit();
    }

    public String getUserEmail() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAI, "");
    }

    public void setUserEmail(String email) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor mEditor = sharedPreferences.edit();

        mEditor.putString(KEY_EMAI, email);
        mEditor.commit();
    }

    public String getUserPhone() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PHON, "");
    }

    public void setUserPhone(String email) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor mEditor = sharedPreferences.edit();

        mEditor.putString(KEY_PHON, email);
        mEditor.commit();
    }
}
