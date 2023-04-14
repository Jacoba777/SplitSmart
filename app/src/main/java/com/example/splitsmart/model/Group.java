package com.example.splitsmart.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.splitsmart.data.SQLiteManager;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;

public class Group implements Serializable {
    public final long id;
    public final String name;
    private final String password;
    public final String joinCode;
    public ArrayList<User> members;
    public User owner;

    private static final String tableName = "[group]";
    private final static int JOIN_CODE_LENGTH = 4;

    public Group(long id, String name, String password, String joinCode, ArrayList<User> members, User owner) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.joinCode = joinCode;
        this.members = members;
        this.owner = owner;
    }

    /**
     * @param context the curretn activity context.
     * @param id the databse id of the group to select.
     * @return the Group object with the specified id.
     */
    public static Group selectGroup(Context context, long id) {
        SQLiteDatabase db = SQLiteManager.getInstance(context).getReadableDatabase();
        String[] select = {"id", "name", "password", "joincode", "ownerid"};
        String where = "id=?";
        String[] args = {""+id};

        Cursor c = db.query("[group]", select, where, args, null, null, null);
        if(c == null || c.getCount() == 0)  return null;
        else {
            c.moveToFirst();
            int i = 0;
            Group group = new Group(c.getLong(i++), c.getString(i++), c.getString(i++), c.getString(i++), new ArrayList<>(), null);
            group.owner = User.selectUser(context, c.getLong(i));
            c.close();
            group.members = User.selectUsersByGroup(context, group);
            return group;
        }
    }

    /**
     * Attempts to select a group from the database based on the name, password, and join code.
     * @return the group if found, otherwise null.
     */
    public static Group selectGroupByName(Context context, String name, String password, String joinCode) {
        SQLiteDatabase db = SQLiteManager.getInstance(context).getReadableDatabase();
        String[] select = {"id", "name", "password", "joincode", "ownerid"};
        String where = "name=? AND password=? AND joincode=?";
        String[] args = {name, password, joinCode};

        Cursor c = db.query(tableName, select, where, args, null, null, null);
        if(c == null || c.getCount() == 0)  return null;
        else {
            c.moveToFirst();
            int i = 0;
            Group group = new Group(c.getLong(i++), c.getString(i++), c.getString(i++), c.getString(i++), new ArrayList<>(), null);
            group.owner = User.selectUser(context, c.getLong(i));
            c.close();
            group.members = User.selectUsersByGroup(context, group);
            return group;
        }
    }

    /**
     * Creates a new group, gives it a unique join code, and then saves it to the database.
     * @param context the current activity context.
     * @param name the name of the group.
     * @param password the password for the group.
     * @param owner the user who created/owns the group.
     * @return the newly created group.
     */
    public static Group createGroup(Context context, String name, String password, User owner) {
        String joinCode = generateRandomJoinCode();

        SQLiteDatabase db = SQLiteManager.getInstance(context).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("password", password);
        cv.put("ownerid", owner.id);
        cv.put("joincode", joinCode);

        long id = db.insert(tableName, null, cv);
        Group group = new Group(id, name, password, joinCode, new ArrayList<>(), owner);
        group.addMember(context, owner);

        return group;
    }

    /**
     * Adds a member to the group.
     */
    public void addMember(Context context, User user) {
        user.groupid = this.id;
        user.update(context);

        this.members.add(user);
    }

    /**
     * @return a random 4-character code. Example: J0T9
     */
    private static String generateRandomJoinCode() {
        final String validChars = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        SecureRandom sr = new SecureRandom();

        for (int i = 0; i < Group.JOIN_CODE_LENGTH; i++) {
            char nextChar = validChars.charAt(sr.nextInt(validChars.length()));
            sb.append(nextChar);
        }
        return sb.toString();
    }
}
