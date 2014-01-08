package org.mifosx.mobile.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

public class UserFunctions {
    private JSONParser jsonParser;

    public UserFunctions() {
        jsonParser = new JSONParser();
    }

    /**
     * function make Login Request
     *
     * @param mifosInstance
     * @param username
     * @param password
     */
    public JSONObject loginUser(String mifosInstance, String username, String password) {
        JSONObject json = jsonParser.getJSONFromUrl(mifosInstance + "/authentication?username=" + username + "&password=" + password);
        return json;
    }

    public boolean isUserLoggedIn(Context context) {
        DataBaseHandler db = new DataBaseHandler(context);
        int count = db.getRowCount();
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * Function to logout user Reset Database
     */
    public boolean logoutUser(Context context) {
        DataBaseHandler db = new DataBaseHandler(context);
        db.resetTables();
        return true;
    }

}
