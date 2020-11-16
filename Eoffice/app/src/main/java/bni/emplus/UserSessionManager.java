package bni.emplus;

/**
 * Created by ZulfahPermataIlliyin on 6/25/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class UserSessionManager {
    // Shared Preferences reference
    SharedPreferences pref;

    // Editor reference for Shared preferences
    Editor editor;
    Editor editorRole;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREFER_NAME = "AndroidExamplePref";

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "npp";
    public static final String KEY_ROLE = "role";
    public static final String KEY_UNIT = "unit";
    public static final String KEY_EMPID = "empid";
    public static final String KEY_STATID = "statid";
    public static final String KEY_ERID = "erid";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_LOADINGMORE = "LoadingMore";
    public static final String KEY_MenuMobile = "MenuMobile";

    // Constructor
    public UserSessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Create login session
    public void createUserLoginSession(String npp, String empid){
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, npp);
        editor.putString(KEY_EMPID, empid);

        // commit changes
        editor.commit();
    }

    //Create role session
    public void createUserRoleSession(String role, String unit, String statid, String erid, String menumobile){
        // Storing name in pref
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_UNIT, unit);
        editor.putString(KEY_STATID, statid);
        editor.putString(KEY_ERID, erid);
        editor.putString(KEY_MenuMobile, menumobile);

        // commit changes
        editor.commit();

    }

    //Create role session
    public void createLoadingMore(String loadingMore){
        // Storing name in pref
        editor.putString(KEY_LOADINGMORE, loadingMore);

        // commit changes
        editor.commit();

    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     * */
    public boolean checkLogin(){
        // Check login status
        if(!this.isUserLoggedIn()){

            // user is not logged in redirect him to Login Activity
            /*Intent i = new Intent(_context, log.class);

            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);*/

            return true;
        }
        return false;
    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserNPP(){

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();

        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMPID, pref.getString(KEY_EMPID, null));

        // return user
        return user;
    }

    public HashMap<String, String> getLoadingMore(){

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();

        // loading more
        user.put(KEY_LOADINGMORE, pref.getString(KEY_LOADINGMORE, null));

        // return user
        return user;
    }

    public HashMap<String, String> getToken(){

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();

        // user token
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));

        // return user
        return user;
    }

    public HashMap<String, String> getUserRole(){

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();

        // user name
        user.put(KEY_UNIT, pref.getString(KEY_UNIT, null));
        user.put(KEY_ROLE, pref.getString(KEY_ROLE, null));
        user.put(KEY_ERID, pref.getString(KEY_ERID, null));
        user.put(KEY_STATID, pref.getString(KEY_STATID, null));
        user.put(KEY_MenuMobile, pref.getString(KEY_MenuMobile, null));


        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){

        // Clearing all user data from Shared Preferences
        editor.clear();

        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, log.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


    // Check for login
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
