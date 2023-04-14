package com.example.splitsmart.activity.group;

import android.content.Intent;
import android.os.Bundle;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.activity.MainActivity;
import com.example.splitsmart.model.UserSession;

public class GroupNoneActivity extends ImprovedBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_none);
        setListeners();
    }

    void setListeners() {
        setListener(R.id.button_join_group, this::onClickButtonJoinGroup);
        setListener(R.id.button_create_group, this::onClickButtonCreateGroup);
        setListener(R.id.button_sign_out, this::onClickButtonSignOut);
    }

    private void onClickButtonJoinGroup() {
        Intent login = new Intent(this, GroupJoinActivity.class);
        startActivity(login);
        finish();
    }

    private void onClickButtonCreateGroup() {
        startActivity(new Intent(this, GroupCreateActivity.class));
        finish();
    }

    private void onClickButtonSignOut() {
        UserSession.logout();
        Intent login = new Intent(this, MainActivity.class);
        startActivity(login);
        finish();
    }
}