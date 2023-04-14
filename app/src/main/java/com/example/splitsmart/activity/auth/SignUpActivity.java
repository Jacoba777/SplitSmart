package com.example.splitsmart.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.activity.group.GroupNoneActivity;
import com.example.splitsmart.model.User;
import com.example.splitsmart.model.UserSession;

public class SignUpActivity extends ImprovedBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setListeners();
    }

    void setListeners() {
        setListener(R.id.button_sign_up, this::onClickButtonSignUp);
        setListener(R.id.button_back, this::onClickBack);
    }

    /**
     * Validates user input, then attempts to insert a new User into the database.
     * @return boolean of whether or not the User record insertion was successful or not
     */
    private boolean tryCreateUserFromWidgets() {
        String username = ((EditText)findViewById(R.id.edittext_username)).getText().toString();
        String password1 = ((EditText)findViewById(R.id.edittext_password)).getText().toString();
        String password2 = ((EditText)findViewById(R.id.edittext_password2)).getText().toString();

        if(username.isEmpty()) {
            toast("Username cannot be empty");
            return false;
        }

        if(password1.isEmpty()) {
            toast("Password cannot be empty");
            return false;
        }

        if(!password1.equals(password2)) {
            toast("Passwords do not match!");
            return false;
        }

        try {
            User user = User.insertUser(this, username, password1);
            UserSession.login(user, null);
        } catch (IllegalArgumentException e) {
            toast(e.getMessage());
            return false;
        }
        return true;
    }

    private void onClickButtonSignUp() {
        if (!tryCreateUserFromWidgets()) return;

        Intent login = new Intent(this, GroupNoneActivity.class); // New user will always have no group yet, no need to check
        startActivity(login);
        finish();
    }

    void onClickBack() {
        finish();
    }
}
