package com.example.splitsmart.activity.reporting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.model.Balances;
import com.example.splitsmart.model.Group;
import com.example.splitsmart.model.Payment;
import com.example.splitsmart.model.User;
import com.example.splitsmart.model.UserSession;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

public class BalancesMakePaymentActivity extends ImprovedBaseActivity {

    Balances balances;
    ArrayList<User> potentialRecipients;
    Spinner spinnerRecipient;
    TextView tvMaxPaymentMessage;
    EditText editTextPaymentAmount;
    User user;
    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balances_make_payment);

        spinnerRecipient = findViewById(R.id.spinner_recipient);
        tvMaxPaymentMessage = findViewById(R.id.tv_max_payment);
        editTextPaymentAmount = findViewById(R.id.edittext_payment_amount);

        user = UserSession.getUser();
        group = UserSession.getGroup();

        balances = (Balances) getIntent().getSerializableExtra("balances");

        calcPotentialRecipientMaxPayments();
        setRecipientSpinnerBehavior();
        setListeners();
    }

    void setListeners() {
        setListener(R.id.button_make_payment, this::onClickMakePayment);
        setListener(R.id.button_back, this::onClickBack);
    }

    /**
     * Get the list of group members that the user owes money to.
     */
    void calcPotentialRecipientMaxPayments() {
        potentialRecipients = (ArrayList<User>) group.members.stream().filter(u ->
                u.id != user.id && balances.getBalance(user, u) > 0.005).collect(Collectors.toList());
    }

    /**
     * Sets spinner options to all of the group members that the user owes money to.
     * Also sets the behavior when a new user is selected.
     */
    void setRecipientSpinnerBehavior() {
        ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, potentialRecipients);
        spinnerRecipient.setAdapter(arrayAdapter);

        spinnerRecipient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                User recipient = potentialRecipients.get(i);
                tvMaxPaymentMessage.setText(String.format(Locale.US, "You currently owe %s $%.2f. Payments exceeding this amount will be rejected.",
                        recipient, balances.getBalance(user, recipient)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    /**
     * Validates the user's input, then attempts to write the payment to the database.
     * @return true if the payment was successfully added to the database. False otherwise.
     */
    private boolean tryInsertPaymentFromWidgets() {
        User recipient = (User) spinnerRecipient.getSelectedItem();
        float paymentAmount;
        float maxPaymentAmount = balances.getBalance(user, recipient);

        try {
            paymentAmount = Float.parseFloat(editTextPaymentAmount.getText().toString());
        } catch(NumberFormatException ex) {
            toast("Payment amount is not a number");
            return false;
        }
        if(paymentAmount <= 0) {
            toast("Payment amount must be a positive number");
            return false;
        }
        if(paymentAmount > maxPaymentAmount) {
            toast(String.format(Locale.US, "Payment amount to %s must be no more than $%.2f", recipient, maxPaymentAmount));
            return false;
        }

        Payment payment = new Payment(group, user, recipient, paymentAmount, Date.from(Instant.now()));
        payment.insertOrUpdate(this);
        return true;
    }

    void onClickMakePayment() {
        if (!tryInsertPaymentFromWidgets()) return;

        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    void onClickBack() {
        finish();
    }
}