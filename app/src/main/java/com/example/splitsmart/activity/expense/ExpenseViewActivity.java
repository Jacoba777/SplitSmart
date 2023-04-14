package com.example.splitsmart.activity.expense;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.activity.expense.receipt.ExpenseViewReceiptActivity;
import com.example.splitsmart.data.SQLiteManager;
import com.example.splitsmart.model.Expense;
import com.example.splitsmart.model.ImageReceipt;

import java.util.Locale;

public class ExpenseViewActivity extends ImprovedBaseActivity {
    Expense expense;
    Button buttonShowReceipt;
    TextView textViewNoReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_view);

        buttonShowReceipt = findViewById(R.id.buttonShowReceipt);
        textViewNoReceipt = findViewById(R.id.textViewNoReceipt);

        expense = (Expense) getIntent().getSerializableExtra("expense");

        setListeners();
        setWidgetsFromExpenseDetails();
        displayReceiptButtonIfExists();
    }

    void setListeners() {
        setListener(R.id.button_back, this::onClickButtonBack);
        setListener(R.id.buttonShowReceipt, this::onClickShowReceipt);
    }

    /**
     * Show/hide widgets depending on if the receipt exists or not
     */
    void displayReceiptButtonIfExists() {
        Bitmap receipt = ImageReceipt.getReceiptBitmapFromDatabase(this, expense);

        buttonShowReceipt.setVisibility(receipt == null ? View.GONE : View.VISIBLE);
        textViewNoReceipt.setVisibility(receipt == null ? View.VISIBLE : View.GONE);
    }

    void setWidgetsFromExpenseDetails() {
        TextView tvExpenseDesc = findViewById(R.id.tv_expense_desc);
        TextView tvExpensePayer = findViewById(R.id.tv_expense_payer);
        TextView tvExpenseDate = findViewById(R.id.tv_expense_date);
        TextView tvExpenseTotalAmount = findViewById(R.id.tv_expense_total_amount);
        ListView listViewSplits = findViewById(R.id.list_view_splits);

        tvExpenseTotalAmount.setText(String.format(Locale.US, "Total Amount: $%.2f", expense.amount));
        tvExpenseDate.setText(String.format("Date: %s", SQLiteManager.dateFormat.format(expense.date)));
        tvExpenseDesc.setText(expense.desc);
        tvExpensePayer.setText(String.format("Paid by %s", expense.payer.username));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expense.getExpenseSplitsPrettyString());
        listViewSplits.setAdapter(arrayAdapter);
    }

    private void onClickShowReceipt() {
        Intent intent = new Intent(this, ExpenseViewReceiptActivity.class);
        intent.putExtra("expense", expense);
        startActivity(intent);
    }

    private void onClickButtonBack() {
        finish();
    }
}
