package com.example.splitsmart.activity.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.activity.reporting.ReportTypeSelectActivity;
import com.example.splitsmart.activity.expense.create_steps.ExpenseCreateActivity;
import com.example.splitsmart.model.Group;
import com.example.splitsmart.model.User;
import com.example.splitsmart.model.UserSession;

public class GroupMainActivity extends ImprovedBaseActivity {

    User user;
    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);

        user = UserSession.getUser();
        group = UserSession.getGroup();

        setListeners();
        setTextViewsFromGroupDetails();
        displayManageGroupButtonIfGroupOwner();
    }

    void setTextViewsFromGroupDetails() {
        ((TextView)findViewById(R.id.message)).setText(group.name);
        ((TextView)findViewById(R.id.welcome_message)).setText(String.format("Welcome back, %s!", user.username));
    }

    void displayManageGroupButtonIfGroupOwner() {
        Button buttonManageGroup = findViewById(R.id.button_manage_group);
        buttonManageGroup.setVisibility(group.owner.id == user.id ? View.VISIBLE : View.GONE);
    }

    void setListeners() {
        setListener(R.id.button_create_expense, this::onClickButtonCreateExpense);
        setListener(R.id.button_reporting, this::onClickButtonReporting);
        setListener(R.id.button_manage_group, this::onClickButtonManageGroup);
        setListener(R.id.button_logout, this::onClickLogOut);
    }

    void onClickButtonCreateExpense() {
        startActivity(new Intent(this, ExpenseCreateActivity.class));
    }

    void onClickButtonReporting() {
        startActivity(new Intent(this, ReportTypeSelectActivity.class));
    }

    void onClickButtonManageGroup() {
        startActivity(new Intent(this, GroupManageActivity.class));
    }

    void onClickLogOut() {
        finish();
    }
}