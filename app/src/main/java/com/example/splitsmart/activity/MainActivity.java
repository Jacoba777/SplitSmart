package com.example.splitsmart.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.activity.auth.LoginActivity;
import com.example.splitsmart.activity.auth.SignUpActivity;

public class MainActivity extends ImprovedBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListeners();
    }

    void setListeners() {
        setListener(R.id.button_log_in, this::onClickButtonLogIn);
        setListener(R.id.button_sign_up, this::onClickButtonSignUp);
    }

    private void onClickButtonLogIn() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void onClickButtonSignUp() {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}