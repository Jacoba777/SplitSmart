package com.example.splitsmart.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.splitsmart.data.SQLiteManager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A command pattern that represents a filtered query for a list of expenses.
 */
public class ExpenseFilter implements Serializable {
    Group group;
    User requester;
    public String amountMin;
    public String amountMax;
    public String dateMin;
    public String dateMax;
    public String desc;
    public User payer;
    public boolean relevantToMeOnlyFlag;

    String where;
    ArrayList<String> args;

    public ExpenseFilter() {
        this.group = null;
        this.requester = null;
        this.payer = null;
        this.relevantToMeOnlyFlag = false;

        this.where = "";
        this.args = new ArrayList<>();
    }

    /**
     * Executes the filter in the database.
     * @param context the current activity context.
     * @return a list of expenses that matched all filters.
     */
    public ArrayList<Expense> search(Context context) {
        SQLiteDatabase db = SQLiteManager.getInstance(context).getReadableDatabase();
        String[] select = {"id", "groupid", "payerid", "amount", "date", "desc"};

        ArrayList<Expense> expenses = new ArrayList<>();
        Cursor c = db.query(Expense.tableName, select, this.where, this.args.toArray(new String[]{}), null, null, "date DESC");
        while(c.moveToNext()) {
            int i = 0;
            Expense expense = new Expense(context, c.getLong(i++), c.getLong(i++), c.getLong(i++), c.getFloat(i++), c.getString(i++), c.getString(i));
            if(!this.relevantToMeOnlyFlag || expense.getUserShare(this.requester) > 0.005) {
                expenses.add(expense);
            }
        }
        c.close();
        return expenses;
    }
}
