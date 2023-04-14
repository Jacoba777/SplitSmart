package com.example.splitsmart.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Database access singleton.
 */
public class SQLiteManager extends SQLiteOpenHelper {

    private static SQLiteManager instance;
    private static final String DATABASE_NAME = "SplitSmartDB";
    private static final int DATABASE_VERSION = 6;

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private SQLiteManager(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteManager getInstance(Context context) {
        if(instance == null) {
            instance = new SQLiteManager(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE user ([id] INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, groupid INTEGER)");
        sqLiteDatabase.execSQL("CREATE TABLE [group] ([id] INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, password TEXT, joincode TEXT, ownerid INTEGER)");
        sqLiteDatabase.execSQL("CREATE TABLE expense ([id] INTEGER PRIMARY KEY AUTOINCREMENT, groupid INTEGER, payerid INTEGER, amount NUMERIC, date TEXT, [desc] TEXT, imagereceipt BLOB)");
        sqLiteDatabase.execSQL("CREATE TABLE userXexpense (userid INTEGER, expenseid INTEGER, splittype NUMERIC, amount NUMERIC, PRIMARY KEY(userid, expenseid))");
        sqLiteDatabase.execSQL("CREATE TABLE payment ([id] INTEGER PRIMARY KEY AUTOINCREMENT, groupid INTEGER, payerid INTEGER, recipientid INTEGER, amount NUMERIC, date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
