package com.example.splitsmart.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.splitsmart.data.SQLiteManager;

import java.io.ByteArrayOutputStream;

public class ImageReceipt {
    Bitmap imageBitmap;

    public ImageReceipt(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    /**
     * Updates the bitmap BLOB for an expense receipt.
     * @param context the current activity context.
     * @param expense the expense the receipt belongs to. Must already be in the database.
     */
    public void updateExpenseReceipt(Context context, Expense expense) {
        if(expense.id == 0) throw new IllegalArgumentException("Expense must already be inserted into database before adding a receipt");
        SQLiteDatabase db = SQLiteManager.getInstance(context).getWritableDatabase();

        ContentValues cv = new ContentValues();
        if(imageBitmap != null) {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 0, io);
            byte[] encodedImageReceipt = io.toByteArray();
            System.out.println("image is " + encodedImageReceipt.length + " bytes long");
            cv.put("imagereceipt", encodedImageReceipt);
        }

        String where = "id=?";
        String[] args = {""+expense.id};
        db.update(Expense.tableName, cv, where, args);
    }

    /**
     * Gets the receipt image for an expense.
     * @param context the current activity context.
     * @param expense the expense the receipt belongs to. Must already be in the database.
     * @return the receipt image for the expense.
     */
    public static Bitmap getReceiptBitmapFromDatabase(Context context, Expense expense) {
        SQLiteDatabase db = SQLiteManager.getInstance(context).getReadableDatabase();
        String[] select = {"imagereceipt"};
        String where = "id=?";
        String[] args = {""+expense.id};

        Cursor c = db.query(Expense.tableName, select, where, args, null, null, null);
        if(c.moveToNext()) {
            byte[] imageReceiptBytes = c.getBlob(0);
            c.close();
            if(imageReceiptBytes != null && imageReceiptBytes.length > 0) {
                return BitmapFactory.decodeByteArray(imageReceiptBytes, 0, imageReceiptBytes.length);
            }
        }
        return null;
    }
}
