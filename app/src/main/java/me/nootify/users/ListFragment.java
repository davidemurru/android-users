package me.nootify.users;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import me.nootify.users.MainActivity.AsyncCallback;
import me.nootify.users.MainActivity.Subject;
import me.nootify.users.UserRecyclerViewAdapter.ActionCommand;
import me.nootify.users.data.User;
import me.nootify.users.data.UserProvider;

/**
 * Encapsulates fetching the users and displaying it as a listview layout.
 */
public class ListFragment extends Fragment implements Subject, ActionCommand, SwipeRefreshLayout.OnRefreshListener {

    public interface Observer {
        void update(List data);
    }

    private List<Observer> observers;
    private UserRecyclerViewAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<User> users;
    private UserRecyclerViewAdapter.ActionCommand userCallback;


    public static Fragment newInstance() {

        Fragment fragment;
        fragment = new ListFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        observers = new ArrayList<>();

        // Add this line in order to handle menu events (search).
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_main, container, false);

        setupRecyclerView(rootView);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return rootView;
    }

    private void setupRecyclerView(View rootView) {

        // create the adapter for the listView previously created.
        adapter = new UserRecyclerViewAdapter(getContext(), R.layout.list_item);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter.addActionCommand(this);
        userCallback = new UserActionCallback(getContext(), (Subject) this);

        onRefresh();
    }

    @Override
    public void onRefresh() {

        setTitleApp(getResources().getString(R.string.app_name));

        // show the refresh spinner.
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(true);

        prepareUserList();

        // hide the refresh spinner.
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
    }

    protected void prepareUserList() {

        getUsersData(new AsyncCallback<List<User>>() {

            @Override
            public void onPostExecute(List<User> items) {

                users = items;

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // We are going to invalidate the menu for enable or disable the search button.
                        getActivity().supportInvalidateOptionsMenu();

                        if (users == null || users.size() == 0) {

                            Utilities.showWarning(getView(), getString(R.string.warning_message_no_items), new Callable<Void>() {

                                @Override
                                public Void call() {

                                    onRefresh();

                                    return null;
                                }
                            });

                        } else {

                            adapter.setItems(users);
                            adapter.notifyDataSetChanged();

                        }

                        Utilities.showFadeView(getActivity().getApplicationContext(), getView().findViewById(R.id.listview), getView().findViewById(R.id.loaderview));
                    }
                });

                notifyObservers();
            }
        });
    }

    private void getUsersData(final AsyncCallback<List<User>> callback) {
        UserProvider dataProvider = ((MApplication) getActivity().getApplication()).getUserProvider();
        dataProvider.getUsersData(callback);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);

        final SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.search));

        if (users == null || users.size() == 0) {

            // Whether the users list is empty the search button is disabled and hidden.
            menu.findItem(R.id.search).setEnabled(false)
                    .setVisible(false);

        } else {

            // The search button is enabled and showed.
            menu.findItem(R.id.search).setEnabled(true)
                    .setVisible(true);

            searchView
                    .setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                        @Override
                        public boolean onQueryTextSubmit(
                                String filter) {

                            if (!searchView.isIconified())
                                filterUsers(filter);

                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(
                                String filter) {

                            if (!searchView.isIconified()) {
                                filterUsers(filter);
                            }

                            return false;
                        }

                    });
        }
    }

    // Filter the list of the users showed in the listview.
    private void filterUsers(String filter) {

        if (users == null)
            return;

        final List<User> usersFiltered = new ArrayList<>();
        final String queryLowerCase = filter.toLowerCase();

        Iterator it = users.iterator();
        while (it.hasNext()) {
            User user = (User) it.next();
            if (user.getName().toLowerCase().contains(queryLowerCase) ||
                    user.getEmail().toLowerCase().contains(queryLowerCase)) {
                usersFiltered.add(user);
            }
        }

        adapter.setItems(usersFiltered);
        adapter.notifyDataSetChanged();
    }

    // Set the title of the app in the ActionBar.
    public void setTitleApp(String titleApp) {
        getActivity().setTitle(titleApp);
    }

    @Override
    public void onDestroy() {

        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout = null;

        if (users != null)
            users = null;

        removeAllObservers();

        super.onDestroy();
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    private void removeAllObservers() {
        for (Observer o : observers) {
            removeObserver(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        int i = observers.indexOf(o);
        if (i >= 0) {
            observers.remove(i);
        }
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(users);
        }
    }

    @Override
    public void execute(int index) {
        userCallback.execute(index);

        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

}
