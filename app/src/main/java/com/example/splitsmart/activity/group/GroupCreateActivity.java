package com.example.splitsmart.activity.group;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.model.Group;
import com.example.splitsmart.model.User;
import com.example.splitsmart.model.UserSession;

public class GroupCreateActivity extends ImprovedBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        setListeners();
    }

    void setListeners() {
        setListener(R.id.button_create_group, this::onClickButtonCreateGroup);
    }

    /**
     * Validates user input, then attempts to create a new group in the database.
     * Has the side-effect of setting the user's group to the newly created one.
     * @return true if the group was created successfully; False otherwise
     */
    private boolean tryCreateGroupFromWidgets() {
        String name = ((EditText)findViewById(R.id.edittext_group_name)).getText().toString();
        String password = ((EditText)findViewById(R.id.edittext_group_password)).getText().toString();
        String password2 = ((EditText)findViewById(R.id.edittext_group_password2)).getText().toString();

        if(name.isEmpty()) {
            toast("Group name cannot be empty");
            return false;
        }

        if(password.isEmpty()) {
            toast("Password cannot be empty");
            return false;
        }

        if(!password.equals(password2)) {
            toast("Passwords do not match!");
            return false;
        }

        User user = UserSession.getUser();
        Group group = Group.createGroup(this, name, password, user);
        UserSession.login(user, group); // Need to re-log in to refresh the session group
        return true;
    }

    private void onClickButtonCreateGroup() {
        if (!tryCreateGroupFromWidgets()) return;

        Intent login = new Intent(this, GroupMainActivity.class);
        startActivity(login);
        finish();
    }
}
