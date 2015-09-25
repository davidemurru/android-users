package me.nootify.users;

import android.app.Application;

import me.nootify.users.data.UserProvider;

/**
 * Created by davide on 23/09/15.
 */
public class MApplication extends Application {

    private UserProvider mUserProvider;

    @Override
    public void onCreate() {
        super.onCreate();

        mUserProvider = UserProvider.getInstance(this);
    }


    public UserProvider getUserProvider() {
        return mUserProvider;
    }
}
