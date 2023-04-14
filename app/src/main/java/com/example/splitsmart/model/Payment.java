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

public class Payment implements Serializable {
    public long id;
    public Group group;
    public User payer;
    public User recipient;
    public float amount;
    public Date date;

    private static final String tableName = "payment";

    /**
     * Constructor for a payment that is not in the database yet.
     */
    public Payment(Group group, User payer, User recipient, float amount, Date date) {
        this.id = 0;
        this.group = group;
        this.payer = payer;
        this.recipient = recipient;
        this.amount = amount;
        this.date = date;
    }

    /**
     * Constructor for a payment from a payment database record.
     */
    public Payment(Context context, long id, long groupId, long payerId, long recipientId, float amount, String dateStr) {
        this.id = id;
        this.group = Group.selectGroup(context, groupId);
        this.payer = User.selectUser(context, payerId);
        this.recipient = User.selectUser(context, recipientId);
        this.amount = amount;
        try {
            this.date = SQLiteManager.dateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Database selector that gets all payments for a group.
     * @param context the current activity context.
     * @param group the group to get payments from.
     * @return a list of payments that hte group has generated.
     */
    public static ArrayList<Payment> selectAllByGroup(Context context, Group group) {
        SQLiteDatabase db = SQLiteManager.getInstance(context).getReadableDatabase();
        String[] select = {"id", "groupid", "payerid", "recipientid", "amount", "date"};
        String where = "groupid=?";
        String[] args = {""+group.id};

        ArrayList<Payment> payments = new ArrayList<>();
        Cursor c = db.query(tableName, select, where, args, null, null, null);
        while(c.moveToNext()) {
            int i = 0;
            Payment payment = new Payment(context, c.getLong(i++), c.getLong(i++), c.getLong(i++), c.getLong(i++), c.getFloat(i++), c.getString(i));
            payments.add(payment);
        }
        c.close();
        return payments;
    }

    /**
     * Puts a payment into the database. It is an insertion if it doesn't exist yet, and an update if it does.
     * @param context the current activity context.
     */
    public void insertOrUpdate(Context context) {
        boolean exists = this.id > 0;
        SQLiteDatabase db = SQLiteManager.getInstance(context).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("groupid", group.id);
        cv.put("payerid", payer.id);
        cv.put("recipientid", recipient.id);
        cv.put("amount", amount);
        cv.put("date", SQLiteManager.dateFormat.format(date));

        if(exists) {
            String where = "id=?";
            String[] args = {""+this.id};
            db.update(tableName, cv, where, args);
        } else {
            this.id = db.insert(tableName, null, cv);
        }
    }
}
