package me.nootify.users.Data;

import android.content.Context;

import org.json.JSONException;

import java.util.Iterator;
import java.util.List;

import ly.apps.android.rest.client.Callback;
import ly.apps.android.rest.client.Response;
import ly.apps.android.rest.client.RestClient;
import ly.apps.android.rest.client.RestClientFactory;
import ly.apps.android.rest.client.RestServiceFactory;
import me.nootify.users.MainActivity.AsyncCallback;
import me.nootify.users.R;
import me.nootify.users.Service.IRestAPI;
import me.nootify.users.Service.ServiceResponse;
import me.nootify.users.Utilities;

/**
 * Helper Class for retrieve the users data from local storage (DB)
 * or from the API backend.
 */
public class UserProvider {

    // Tread-Safe because is called from the onStart of the Application.
    private static UserProvider mInstance = null;
    private Context mContext;


    public static UserProvider getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new UserProvider(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private UserProvider(Context context) {
        this.mContext = context;
    }

    public void getUsersData(final AsyncCallback<List<User>> callback) {

        // we check for the availability of the internet connection.
        final boolean isNetworkAvailable = Utilities.isNetworkAvailable(mContext);

        if (!isNetworkAvailable) {

            // we retrieve data from the local storage.
            new Thread(new Runnable() {
                @Override
                public void run() {

                    final UserContract dbContract = new UserContract(mContext);
                    List<User> users = dbContract.getAllUsers();

                    callback.onPostExecute(users);

                }
            }).start();

        } else {

            // we retrieve data from the API backend.
            RestClient client = RestClientFactory.defaultClient(mContext);
            IRestAPI api = RestServiceFactory.getService(mContext.getResources().getString(R.string.api_baseurl), IRestAPI.class, client);

            api.getList(mContext.getResources().getString(R.string.api_get_users), new Callback<ServiceResponse>() {

                @Override
                public void onResponse(final Response<ServiceResponse> response) {

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            List<User> users = null;

                            if (response.getStatusCode() == 200 && response.getResult() != null) {

                                ServiceResponse result = response.getResult();

                                users = result.getUsers();

                                if (users != null) {

                                    int index = 0;

                                    // We are going to add an incremental index for every user.
                                    Iterator it = users.iterator();
                                    while (it.hasNext()) {
                                        User user = (User) it.next();
                                        user.setId(++index);
                                    }

                                    // we preparing for cache the user data into the DB.
                                    final UserContract dbContract = new UserContract(mContext);

                                    try {
                                        dbContract.updateUsers(users);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            callback.onPostExecute(users);

                        }
                    }).start();
                }
            });
        }
    }

}
