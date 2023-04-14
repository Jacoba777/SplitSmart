package com.example.splitsmart.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.group.GroupMainActivity;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.activity.group.GroupNoneActivity;
import com.example.splitsmart.model.Group;
import com.example.splitsmart.model.User;
import com.example.splitsmart.model.UserSession;

public class LoginActivity extends ImprovedBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setListeners();
    }

    void setListeners() {
        setListener(R.id.button_log_in, this::onClickButtonLogIn);
        setListener(R.id.button_back, this::onClickBack);
    }

    /**
     * Validates user input then attempts to select the user from the database
     * @return a User if input data is valid and matches a User record; Else null
     */
    @Nullable
    private User trySelectUserFromWidgets() {
        String username = ((EditText)findViewById(R.id.edittext_username)).getText().toString();
        String password = ((EditText)findViewById(R.id.edittext_password)).getText().toString();

        if(username.isEmpty() || password.isEmpty()) {
            toast("Username and password are required");
            return null;
        }

        User user = User.selectUserByUsername(this, username, password);
        if(user == null) {
            toast("Incorrect username or password");
            return null;
        }

        return user;
    }

    /**
     * Attempts to fetch teh User and their group from the input data. One of three things will happen:
     *  1. Input data is invalid: Stay on current activity, with toast window indicating the login failed
     *  2. User found, but no group: Navigate to the GroupNoneActivity
     *  3. User found, and they are in a group: Navigate to the GroupMainActivity
     */
    private void onClickButtonLogIn() {
        User user = trySelectUserFromWidgets();
        if(user == null) return;

        Group group = Group.selectGroup(this, user.groupid);
        UserSession.login(user, group);

        Class<? extends ImprovedBaseActivity> nextActivity = (group != null ? GroupMainActivity.class : GroupNoneActivity.class);
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
        finish();
    }

    void onClickBack() {
        finish();
    }
}
