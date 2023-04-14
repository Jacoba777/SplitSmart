package com.example.splitsmart.activity.reporting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.model.Balances;
import com.example.splitsmart.model.Group;
import com.example.splitsmart.model.User;
import com.example.splitsmart.model.UserSession;

public class BalancesOwedActivity extends ImprovedBaseActivity {

    Balances balances;
    User user;
    Group group;
    ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balances_owed);

        user = UserSession.getUser();
        group = UserSession.getGroup();

        balances = new Balances(this, group);

        setListeners();
        setPaymentCallbackListener();
        displayBalances();
    }

    void setListeners() {
        setListener(R.id.button_make_payment, this::clickMakePayment);
        setListener(R.id.button_back, this::clickBack);
    }

    void setPaymentCallbackListener() {
        resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    balances = new Balances(this, group);
                    displayBalances();
                }
            }
        );
    }

    void displayBalances() {
        TextView tvBalances = findViewById(R.id.textview_balances);
        String displayText = balances.prettyPrintBalances(UserSession.getUser());
        tvBalances.setText(displayText);
    }

    boolean owesSomeoneMoney(User user) {
        for(User member : group.members) {
            if(member.id != user.id) {
                float balance = balances.getBalance(user, member);
                if(balance > 0.005) {
                    return true;
                }
            }
        }
        return false;
    }

    void clickMakePayment() {
        if(!owesSomeoneMoney(user)) {
            toast("You don't owe anyone money!");
            return;
        }

        Intent intent = new Intent(this, BalancesMakePaymentActivity.class);
        intent.putExtra("balances", balances);
        resultLauncher.launch(intent);
    }

    void clickBack() {
        finish();
    }
}