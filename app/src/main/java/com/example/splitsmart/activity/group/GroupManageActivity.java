package com.example.splitsmart.activity.group;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.data.RemoveMembersAdapter;
import com.example.splitsmart.model.Group;
import com.example.splitsmart.model.User;
import com.example.splitsmart.model.UserSession;

import java.util.ArrayList;

public class GroupManageActivity extends ImprovedBaseActivity {

    Group group;
    User user;

    TextView title;
    TextView joinCodeMessage;
    ListView listViewRemoveUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manage);

        group = UserSession.getGroup();
        user = UserSession.getUser();

        listViewRemoveUsers = findViewById(R.id.listview_remove_users);
        title = findViewById(R.id.message);
        joinCodeMessage = findViewById(R.id.join_code);


        setListeners();
        setTextViewsFromGroupDetails();
        setRemoveMembersListViewBehavior();
    }

    void setListeners() {
        setListener(R.id.button_back, this::onClickBack);
    }

    /**
     * Sets the list of group members who can be removed from the group.
     */
    void setRemoveMembersListViewBehavior() {
        ArrayList<User> members = new ArrayList<>(group.members);
        members.removeIf(u -> u.id == user.id); // Don't let the group owner remove themselves

        RemoveMembersAdapter adapter = new RemoveMembersAdapter(members, this);
        listViewRemoveUsers.setAdapter(adapter);
    }

    void setTextViewsFromGroupDetails() {
        title.setText(String.format("Manage %s", group.name));
        joinCodeMessage.setText(String.format("You group's join code is: %s", group.joinCode));
    }

    private void onClickBack() {
        finish();
    }
}
