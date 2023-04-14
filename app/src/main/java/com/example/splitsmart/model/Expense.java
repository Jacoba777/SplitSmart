package com.example.splitsmart.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.splitsmart.data.SQLiteManager;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

public class Expense implements Serializable {
    public long id;
    public Group group;
    public User payer;
    public final float amount;
    public final Date date;
    public final String desc;
    public ArrayList<ExpenseSplit> expenseSplits;

    public static final String tableName = "expense";

    /**
     * Constructor to be used to generate an expense from a database record
     */
    public Expense(Context context, long id, long groupId, long payerId, float amount, String date, String desc) {
        this.id = id;
        this.payer = User.selectUser(context, payerId);
        this.group = Group.selectGroup(context, groupId);
        this.amount = amount;
        try {
            this.date = SQLiteManager.dateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.desc = desc;
        this.expenseSplits = ExpenseSplit.selectByExpense(context, id);
        validateSplits();
    }

    /**
     * Constructor to generate an expense that is not yet in the database
     */
    public Expense(User payer, float amount, String date, String desc, Group group) {
        this.payer = payer;
        this.group = group;
        this.amount = amount;
        this.desc = desc;
        try {
            this.date = SQLiteManager.dateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.expenseSplits = new ArrayList<>();
    }

    /**
     * Gets the amount of money that a particular user is liable for in this expense
     * @param user the user whose share we are trying to find
     * @return the amount this user is liable for
     */
    public float getUserShare(User user) {
        Optional<ExpenseSplit> es = expenseSplits.stream().filter(e -> e.user.id == user.id).findFirst();
        return es.map(e -> e.amount).orElse(0f);
    }

    /**
     * @return a list of short, user-readable strings that describe each user's share of this exepense
     */
    public ArrayList<String> getExpenseSplitsPrettyString() {
        return (ArrayList<String>) expenseSplits.stream().map(ExpenseSplit::toPrettyString).collect(Collectors.toList());
    }

    /**
     * Goes through each split and determines the exact amount in dollars, as well as the exact percentage of the total for each user in the expense.<br><br>
     *
     * The calculation works as follows:<br>
     *  1. Each user who has a custom amount will have their percentage calculated relative to the total.<br>
     *  2. Each user who has a custom percentage will have their amount in dollars calculated relative to the total.<br>
     *  3. Each user who is using the default (equal) split strategy is ignored at first until all of the custom users are accounted for.<br>
     *    3a. Once all users are accounted for, the remainder of the total not covered by custom-amount users is split evenly between all of the users using the default split strategy.
     *
     * A split is considered "invalid" if any of the following are true:<br>
     *  1. The sum of the percentages of all users does not sum to 100%<br>
     *  2. One or more users have a negative amount<br>
     *
     * @return boolean indicating of the current split arrangement is valid or not.
     */
    public boolean validateSplits() {
        float defaultPool = this.amount;
        ArrayList<ExpenseSplit> defaultUsers = new ArrayList<>();

        for(ExpenseSplit es: this.expenseSplits) {
            switch(es.splitType) {
                case DEFAULT:
                    defaultUsers.add(es);
                    break;
                case FIXED_AMOUNT:
                    es.percentage = (es.amount / this.amount) * 100;
                    defaultPool -= es.amount;
                    break;
                case PERCENTAGE:
                    es.amount = this.amount * es.percentage / 100;
                    defaultPool -= es.amount;
                    break;
            }
        }

        float amountPerDefaultUser = defaultPool / defaultUsers.size();
        float percentPerDefaultUser = (amountPerDefaultUser / this.amount) * 100;
        for(ExpenseSplit es : defaultUsers) {
            es.amount = amountPerDefaultUser;
            es.percentage = percentPerDefaultUser;
        }

        return (defaultUsers.size() > 0 && defaultPool > 0) || Math.abs(defaultPool) < 0.005;
    }

    /**
     * Retrieves all of the expenses for a group from the database.
     * @param context the current activity context.
     * @param group the group whose expenses should be obtained
     * @return all expenses for the specified group
     */
    public static ArrayList<Expense> selectAllByGroup(Context context, Group group) {
        SQLiteDatabase db = SQLiteManager.getInstance(context).getReadableDatabase();
        String[] select = {"id", "groupid", "payerid", "amount", "date", "desc"};
        String where = "groupid=?";
        String[] args = {""+group.id};

        ArrayList<Expense> expenses = new ArrayList<>();
        Cursor c = db.query(tableName, select, where, args, null, null, null);
        while(c.moveToNext()) {
            int i = 0;
            Expense expense = new Expense(context, c.getLong(i++), c.getLong(i++), c.getLong(i++), c.getFloat(i++), c.getString(i++), c.getString(i));
            expenses.add(expense);
        }
        c.close();
        return expenses;
    }

    /**
     * Puts an expense into the database. This is an insertion if it already exists, otherwise it is an update.
     * @param context the current activity context.
     */
    public void insertOrUpdate(Context context) {
        boolean exists = this.id > 0;
        SQLiteDatabase db = SQLiteManager.getInstance(context).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("payerid", payer.id);
        cv.put("groupid", group.id);
        cv.put("amount", amount);
        cv.put("date", SQLiteManager.dateFormat.format(date));
        cv.put("desc", desc);

        if(exists) {
            String where = "id=?";
            String[] args = {""+this.id};
            db.update(tableName, cv, where, args);
        } else {
            this.id = db.insert(tableName, null, cv);
        }

        for(ExpenseSplit es : expenseSplits) {
            es.insert(context, this.id);
        }
    }
}
