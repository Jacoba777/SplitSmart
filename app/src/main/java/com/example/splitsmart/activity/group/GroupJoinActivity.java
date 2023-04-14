package com.example.splitsmart.activity.group;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.model.Group;
import com.example.splitsmart.model.User;
import com.example.splitsmart.model.UserSession;

public class GroupJoinActivity extends ImprovedBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_join);
        setListeners();
    }

    void setListeners() {
        setListener(R.id.button_join_group, this::onClickButtonJoinGroup);
    }

    /**
     * Attempts to join a group based on the user's input.
     * Has side-effect of adding hte user to the group if successful.
     * @return true if user was added to the group successfully. False otherwise.
     */
    private boolean tryJoinGroupFromWidgets() {
        String name = ((EditText)findViewById(R.id.edittext_group_name)).getText().toString();
        String password = ((EditText)findViewById(R.id.edittext_group_password)).getText().toString();
        String joinCode = ((EditText)findViewById(R.id.edittext_join_code)).getText().toString();

        Group g = Group.selectGroupByName(this, name, password, joinCode);
        if(g == null) {
            toast("Could not find a group with these properties!");
            return false;
        }

        User user = UserSession.getUser();
        user.groupid = g.id;
        user.update(this);

        UserSession.login(user, g); // Need to re-log in to refresh the session group
        return true;
    }

    private void onClickButtonJoinGroup() {
        if (!tryJoinGroupFromWidgets()) return;

        startActivity(new Intent(this, GroupMainActivity.class));
        finish();
    }
}
