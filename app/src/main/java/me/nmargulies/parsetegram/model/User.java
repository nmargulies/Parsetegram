package me.nmargulies.parsetegram.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {
    private static final String KEY_USERNAME = "username";
    private static final String KEY_HANDLE = "handle";
    private static final String KEY_PROFILEPICTURE = "profilePicture";

    public String getUsername() {
        return getString(KEY_USERNAME);
    }

    public String getHandle() {
        return getString(KEY_HANDLE);
    }

    public ParseFile getProfPic() {
        return getParseFile(KEY_PROFILEPICTURE);
    }

}
