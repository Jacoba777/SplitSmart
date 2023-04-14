package com.example.splitsmart.activity.reporting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.activity.expense.ExpenseFilterActivity;
import com.example.splitsmart.data.TransactionLogAdapter;
import com.example.splitsmart.model.Expense;
import com.example.splitsmart.model.ExpenseFilter;
import com.example.splitsmart.model.ExpenseFilterBuilder;
import com.example.splitsmart.model.UserSession;

import java.util.ArrayList;

public class TransactionLogActivity extends ImprovedBaseActivity {
    ExpenseFilter filter;
    ActivityResultLauncher<Intent> resultLauncher;
    ListView listViewTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_log);

        listViewTransactions = findViewById(R.id.listview_transactions);
        buildDefaultExpenseFilter();

        setListeners();
        setFilterUpdateListener();
        displayTransactionsFromFilteredQuery();
    }

    /**
     * Runs the ExpenseFilter and displays the results in the Transaction ListView.
     */
    void displayTransactionsFromFilteredQuery() {
        ArrayList<Expense> expenses = filter.search(this);
        TransactionLogAdapter adapter = new TransactionLogAdapter(expenses, this);
        listViewTransactions.setAdapter(adapter);
    }

    /**
     * Sets the default filter for the user.
     * Default settings are to only show transactions from the group that involve the user.
     */
    void buildDefaultExpenseFilter() {
        ExpenseFilterBuilder builder = new ExpenseFilterBuilder();
        builder.setGroup(UserSession.getGroup());
        builder.setRelevantToMeOnly(UserSession.getUser());
        filter = builder.build();
    }

    /**
     * When the user returns to this activity after updating their filter, rerun the filtered query, and display the new results
     */
    void setFilterUpdateListener() {
        resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    filter = (ExpenseFilter) data.getSerializableExtra("filter");
                    displayTransactionsFromFilteredQuery();
                }
            }
        );
    }

    void setListeners() {
        setListener(R.id.button_filters, this::clickFilters);
        setListener(R.id.button_back, this::clickBack);
    }

    void clickFilters() {
        Intent intent = new Intent(this, ExpenseFilterActivity.class);
        intent.putExtra("filter", filter);
        resultLauncher.launch(intent);
    }

    void clickBack() {
        finish();
    }
}