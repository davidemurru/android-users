package me.nootify.users.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for users data.
 */
public class UserDbHelper extends SQLiteOpenHelper {

    private static UserDbHelper mInstance = null;

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "TICTRAC.db";

    // Create a table to hold users data.
    private static final String CREATE_USERS_TABLE = "CREATE TABLE "
            + UserContract.UsersEntry.TABLE_NAME + " ( "
            + UserContract.UsersEntry.COLUMN_ID + " INTEGER PRIMARY KEY, "
            + UserContract.UsersEntry.COLUMN_NAME + " STRING NOT NULL, "
            + UserContract.UsersEntry.COLUMN_EMAIL + " STRING NOT NULL, "
            + UserContract.UsersEntry.COLUMN_INFO + " STRING NOT NULL "
            + " ); ";

    private Context mContext;


    public static UserDbHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new UserDbHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }


    private UserDbHelper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = c;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserContract.UsersEntry.TABLE_NAME);
        onCreate(db);
    }

}
