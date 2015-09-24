package me.nootify.users;

import android.content.Context;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

import me.nootify.users.Data.User;

/**
 * Created by davide on 24/09/15.
 */
public class UserActionCallback implements UserRecyclerViewAdapter.ActionCallback, ListFragment.Observer {

    private int indexUserSelected = ListView.INVALID_POSITION;
    private List<User> users;
    private Context mContext;
    private MainActivity.Subject mObservable;


    public UserActionCallback(MainActivity.Subject observable) {
        this.mObservable = observable;
        this.mObservable.registerObserver(this);
    }

    @Override
    public void onPostExecute(int userId) {

        if (indexUserSelected != ListView.INVALID_POSITION) {
            users.get(indexUserSelected).setSelected(false);
        }

        User searchUser = new User(userId);
        indexUserSelected = Collections.binarySearch(users, searchUser, searchUser.getUserComp());

        User user = users.get(indexUserSelected);
        user.setSelected(true);

        mObservable.notifySubject(user.getName());
    }

    @Override
    public void update(List items) {
        this.users = items;
    }
}
