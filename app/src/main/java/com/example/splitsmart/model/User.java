package com.example.splitsmart.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.example.splitsmart.data.SQLiteManager;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    public final long id;
    public final String username;
    private final String password;
    public long groupid;

    private final static String tableName = "user";

    /**
     * Constructor to create a user from a database record
     */
    public User(long id, String username, String password, long groupid) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.groupid = groupid;
    }

    /**
     * Gets all of the users in a group.
     * @param context the current activity context
     * @param group the group to get members from
     * @return a list of members of the specified group
     */
    public static ArrayList<User> selectUsersByGroup(Context context, Group group) {
        SQLiteDatabase db = SQLiteManager.getInstance(context).getReadableDatabase();
        String[] select = new String[]{"id", "username", "password", "groupid"};
        String where = "groupid=?";
        String[] args = {group.id+""};

        ArrayList<User> users = new ArrayList<>();
        Cursor c = db.query("user", select, where, args, null, null, null);
        if(c == null || c.getCount() == 0)  return users;
        while(c.moveToNext()){
            int i = 0;
            users.add(new User(c.getLong(i++), c.getString(i++), c.getString(i++), c.getLong(i)));
        }
        c.close();
        return users;
    }

    /**
     * Selects a user from the database by id.
     * @return the user matching the specified id.
     */
    public static User selectUser(Context context, long id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid id provided");

        SQLiteDatabase db = SQLiteManager.getInstance(context).getReadableDatabase();
        String[] select = new String[]{"id", "username", "password", "groupid"};
        String where = "id=?";
        String[] args = {""+id};

        Cursor c = db.query(tableName, select, where, args, null, null, null);
        if(c.moveToNext()) {
            int i = 0;
            User user = new User(c.getLong(i++), c.getString(i++), c.getString(i++), c.getLong(i));
            c.close();
            return user;
        }
        return null;
    }

    /**
     * Selects a user form a database by username/password.
     * @return the user matching the specified username/password, if they exist. Otherwise null.
     */
    public static User selectUserByUsername(Context context, String username, String password) {
        if (username.isEmpty()) throw new IllegalArgumentException("Username is required for this selector");

        SQLiteDatabase db = SQLiteManager.getInstance(context).getReadableDatabase();
        String[] select = new String[]{"id", "username", "password", "groupid"};
        ArrayList<String> args = new ArrayList<>();

        String where = "username=?";
        args.add(username);

        if(password != null && !password.isEmpty()) {
            where += " AND password=?";
            args.add(password);
        }

        Cursor c = db.query("user", select, where, args.toArray(new String[0]), null, null, null);
        if(c == null || c.getCount() == 0)  return null;
        else {
            c.moveToFirst();
            int i = 0;
            User user = new User(c.getLong(i++), c.getString(i++), c.getString(i++), c.getLong(i));
            c.close();
            return user;
        }
    }

    /**
     * Inserts a user into the database.
     * @return the newly-created user.
     */
    public static User insertUser(Context context, String username, String password) {
        SQLiteDatabase db = SQLiteManager.getInstance(context).getWritableDatabase();

        if(selectUserByUsername(context, username, null) != null) {
            throw new IllegalArgumentException("A user with this username already exists!");
        }

        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);

        long id = db.insert("user", null, cv);
        return new User(id, username, password, 0);
    }

    /**
     * Updates an existing user in the database.
     * @param context the curretn activity context.
     */
    public void update(Context context) {
        SQLiteDatabase db = SQLiteManager.getInstance(context).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("id", this.id);
        cv.put("username", this.username);
        cv.put("password", this.password);
        cv.put("groupid", this.groupid);

        String where = "id=?";
        String[] args = {""+this.id};

        db.update("user", cv, where, args);
    }

    @NonNull
    @Override
    public String toString() {
        return username;
    }
}
