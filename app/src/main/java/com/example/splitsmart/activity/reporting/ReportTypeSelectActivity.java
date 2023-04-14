package com.example.splitsmart.activity.reporting;

import android.content.Intent;
import android.os.Bundle;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;

public class ReportTypeSelectActivity extends ImprovedBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_type_select);
        setListeners();
    }

    void setListeners() {
        setListener(R.id.button_balances_owed, this::onClickBalancesOwed);
        setListener(R.id.button_transaction_log, this::onClickTransactionLog);
        setListener(R.id.button_back, this::onClickBack);
    }

    void onClickBalancesOwed() {
        startActivity(new Intent(this, BalancesOwedActivity.class));
    }

    void onClickTransactionLog() {
        startActivity(new Intent(this, TransactionLogActivity.class));
    }

    void onClickBack() {
        finish();
    }
}