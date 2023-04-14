package com.example.splitsmart.activity.expense.receipt;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.model.Expense;
import com.example.splitsmart.model.ImageReceipt;

public class ExpenseViewReceiptActivity extends ImprovedBaseActivity {
    ImageView imageViewReceipt;
    Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_receiptview);

        imageViewReceipt = findViewById(R.id.img_receipt);
        expense = (Expense) getIntent().getSerializableExtra("expense");

        setListeners();
        displayReceipt();
    }

    void displayReceipt() {
        Bitmap receipt = ImageReceipt.getReceiptBitmapFromDatabase(this, expense);
        imageViewReceipt.setImageBitmap(receipt);
    }

    void setListeners() {
        setListener(R.id.button_back, this::onClickButtonBack);
    }

    private void onClickButtonBack() {
        finish();
    }
}
