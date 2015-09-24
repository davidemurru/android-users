package me.nootify.users.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Defines table and column names for the user database.
 */
public class UserContract {

    /* Inner class that defines the table contents of the users table */
    public static final class UsersEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "users";

        // The column for the user id integer.
        public static final String COLUMN_ID = "id";
        // The column for the user name.
        public static final String COLUMN_NAME = "name";
        // The column for the user email.
        public static final String COLUMN_EMAIL = "email";
        // The column for the user info.
        public static final String COLUMN_INFO = "info";
    }

    private UserDbHelper dbHelper;

    public UserContract(Context context) {
        try {
            dbHelper = UserDbHelper.getInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Query for retrieve a single user from the user id.
    public Cursor getUser(int id) {

        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = null;
        String selection = UsersEntry.TABLE_NAME +
                "." + UsersEntry.COLUMN_ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(id)};

        final String tableName = UsersEntry.TABLE_NAME;

        Cursor cursor = null;
        cursor = db.query(tableName, projection, selection, selectionArgs, null, null, null);

        return cursor;
    }

    // Query for retrieve all users stored in the DB.
    public List<User> getAllUsers() {

        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final List<User> users = new ArrayList<>();

        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;

        final String tableName = UsersEntry.TABLE_NAME;

        try {

            Cursor cursor = db.query(tableName, projection, selection, selectionArgs, null, null, UsersEntry.COLUMN_ID + " ASC");

            if (cursor != null && cursor.moveToFirst()) {

                do {
                    User user = new User();

                    user.setId(cursor.getInt(cursor.getColumnIndex(UserContract.UsersEntry.COLUMN_ID)));
                    user.setName(cursor.getString(cursor.getColumnIndex(UserContract.UsersEntry.COLUMN_NAME)));
                    user.setEmail(cursor.getString(cursor.getColumnIndex(UserContract.UsersEntry.COLUMN_EMAIL)));
                    user.setInfos(cursor.getString(cursor.getColumnIndex(UserContract.UsersEntry.COLUMN_INFO)));

                    users.add(user);

                } while (cursor.moveToNext());
            }
        } finally {
            _close();
        }

        return users;
    }

    // update the list of users.
    public boolean updateUsers(List<User> users) throws JSONException {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean success = false;

        // Get and insert the new users data into the database
        Vector cVVector = new Vector<ContentValues>(users.size());

        try {
            // we are going to remove all users from the db.
            _cleanModules(db);

            Iterator itr = users.iterator();

            while (itr.hasNext()) {

                User user = (User) itr.next();

                int id = user.getId();
                String name = user.getName();
                String email = user.getEmail();
                String infos = user.getInfos();

                ContentValues userValues = new ContentValues();

                userValues.put(UsersEntry.COLUMN_ID, id);
                userValues.put(UsersEntry.COLUMN_NAME, name);
                userValues.put(UsersEntry.COLUMN_EMAIL, email);
                userValues.put(UsersEntry.COLUMN_INFO, infos);

                cVVector.add(userValues);
            }

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                int returnCount = _bulkInsert(cvArray);

                if (returnCount == cVVector.size())
                    success = true;

            } else {
                // we just clean the users data from the db.
                success = true;
            }

        } finally {
            _close();
        }

        return success;
    }

    // Insert data in DB in burst mode.
    private int _bulkInsert(ContentValues[] values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        final String tableName = UsersEntry.TABLE_NAME;

        db.beginTransaction();

        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tableName, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return returnCount;
    }

    // Remove all users in the DB table.
    private void _cleanModules(SQLiteDatabase database) {

        final String tableName = UsersEntry.TABLE_NAME;

        try {
            database.delete(tableName, null, null);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    // Close the DB.
    private void _close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
