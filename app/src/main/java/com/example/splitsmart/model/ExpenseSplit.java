package com.example.splitsmart.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.splitsmart.data.SQLiteManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Represents a user's share of an expense.
 */
public class ExpenseSplit implements Serializable {
    public User user;
    public SplitType splitType;
    public float amount;
    public float percentage;
    private static final String tableName = "userXexpense";

    /**
     * Constructor to create an ExpenseSplit that does not yet exist in the database.
     */
    public ExpenseSplit(User user, SplitType splitType, float amount) {
        this.user = user;
        this.splitType = splitType;

        switch(splitType) {
            case FIXED_AMOUNT:
                this.amount = amount;
                break;
            case PERCENTAGE:
                this.percentage = amount;
                break;
        }
    }

    /**
     * Constructor to create an ExpenseSplit from a ExpenseSplit database record.
     */
    public ExpenseSplit(Context context, long userId, int splitType, float amount) {
        this.user = User.selectUser(context, userId);
        this.splitType = SplitType.values()[splitType];
        switch(this.splitType) {
            case FIXED_AMOUNT:
                this.amount = amount;
                break;
            case PERCENTAGE:
                this.percentage = amount;
                break;
        }
    }

    /**
     * @return a human-readable short string that describes the expense split.
     */
    public String toPrettyString() {
        return String.format(Locale.US, "%s: $%.2f (%.2f%%)", user.username, amount, percentage);
    }

    /**
     * Gets all of the splits for a particular expense.
     * @param context the current activity context.
     * @param expenseId the database id of the expense.
     * @return the list of expense splits.
     */
    public static ArrayList<ExpenseSplit> selectByExpense(Context context, long expenseId) {
        SQLiteDatabase db = SQLiteManager.getInstance(context).getReadableDatabase();
        String[] select = {"userid", "splittype", "amount"};
        String where = "expenseid=?";
        String[] args = {""+expenseId};

        Cursor c = db.query(tableName, select, where, args, null, null, null);
        if(c == null || c.getCount() == 0)  return null;
        else {
            ArrayList<ExpenseSplit> expenseSplits = new ArrayList<>();
            while(c.moveToNext()){
                int i = 0;
                expenseSplits.add(new ExpenseSplit(context, c.getLong(i++), c.getInt(i++), c.getFloat(i)));
            }
            c.close();
            return expenseSplits;
        }
    }

    /**
     * Inserts an expense into the database.
     * @param context the current activity context.
     * @param expenseId the id of the expense that this split is related to.
     */
    public void insert(Context context, long expenseId) {
        SQLiteDatabase db = SQLiteManager.getInstance(context).getWritableDatabase();

        Float storedAmount = (splitType == SplitType.PERCENTAGE ? percentage :
                splitType == SplitType.FIXED_AMOUNT ? amount :
                0f);

        ContentValues cv = new ContentValues();
        cv.put("userid", user.id);
        cv.put("expenseid", expenseId);
        cv.put("splittype", splitType.ordinal());
        cv.put("amount", storedAmount);

        db.insert(tableName, null, cv);
    }

    @NonNull
    @Override
    public java.lang.String toString() {
        return user.toString();
    }
}

