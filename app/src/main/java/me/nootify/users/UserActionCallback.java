package me.nootify.users;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

import me.nootify.users.ListFragment.Observer;
import me.nootify.users.MainActivity.Subject;
import me.nootify.users.UserRecyclerViewAdapter.ActionCommand;
import me.nootify.users.data.User;

/**
 * Created by davide on 24/09/15.
 */
public class UserActionCallback implements ActionCommand, Observer {

    private int indexUserSelected = ListView.INVALID_POSITION;
    private List<User> users;
    private Context mContext;
    private Subject mObservable;


    public UserActionCallback(Context context, MainActivity.Subject observable) {
        this.mContext = context;
        this.mObservable = observable;

        mObservable.registerObserver(this);
    }

    @Override
    public void execute(int userId) {

        if (indexUserSelected != ListView.INVALID_POSITION) {
            users.get(indexUserSelected).setSelected(false);
        }

        User searchUser = new User(userId);
        indexUserSelected = Collections.binarySearch(users, searchUser, searchUser.getUserComp());

        User user = users.get(indexUserSelected);
        user.setSelected(true);

        ((Activity) mContext).setTitle(user.getName());
    }

    @Override
    public void update(List items) {
        this.users = items;
    }
}
