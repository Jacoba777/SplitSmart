package com.example.splitsmart.activity.expense.create_steps;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.data.SQLiteManager;
import com.example.splitsmart.model.Expense;
import com.example.splitsmart.model.Group;
import com.example.splitsmart.model.ImageReceipt;
import com.example.splitsmart.model.User;
import com.example.splitsmart.model.UserSession;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ExpenseCreateActivity extends ImprovedBaseActivity {
    final Calendar myCalendar = Calendar.getInstance();
    EditText editTextDate;
    ImageView imageViewReceipt;
    ImageReceipt imageReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_create);

        imageViewReceipt = findViewById(R.id.img_receipt);
        imageReceipt = null;

        setListeners();
        setDateListener();
    }

    void setListeners() {
        setListener(R.id.button_next, this::onClickButtonNext);
        setListener(R.id.button_cancel, this::onClickButtonCancel);
    }

    void setDateListener() {
        editTextDate = findViewById(R.id.edittext_date);

        DatePickerDialog.OnDateSetListener dateListener = (view, year, month, day) -> {
            myCalendar.set(year, month, day);
            updateLabel();
        };

        editTextDate.setOnClickListener(view -> new DatePickerDialog(
                ExpenseCreateActivity.this, dateListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH))
            .show());
    }

    private void updateLabel(){
        editTextDate.setText(SQLiteManager.dateFormat.format(myCalendar.getTime()));
    }

    /**
     * Validates user input, then attempts to create a new Expense object.
     * @return the new expense object if data is valid; Else null
     */
    @Nullable
    private Expense tryCreateExpenseFromWidgets() {
        User user = UserSession.getUser();
        Group group = UserSession.getGroup();
        String amountStr = ((EditText)findViewById(R.id.edittext_amount)).getText().toString();
        String dateStr = ((EditText)findViewById(R.id.edittext_date)).getText().toString();
        String desc = ((EditText)findViewById(R.id.edittext_desc)).getText().toString();

        float amount;

        if(amountStr.isEmpty()) {
            toast("Amount is required");
            return null;
        }
        if(dateStr.isEmpty()) {
            toast("Date is required");
            return null;
        }
        if(desc.isEmpty()) {
            toast("Description is required");
            return null;
        }

        try {
            amount = Float.parseFloat(amountStr);
        } catch(NumberFormatException ex) {
            toast("Input amount is not a number");
            return null;
        }

        return new Expense(user, amount, dateStr, desc, group);
    }

    private void onClickButtonNext() {
        Expense exp = tryCreateExpenseFromWidgets();
        if (exp == null) return;
        Intent intent = new Intent(this, ExpenseSplitActivity.class);
        intent.putExtra("expense", exp);
        startActivity(intent);
        finish();

    }

    private void onClickButtonCancel() {
        finish();
    }
}
