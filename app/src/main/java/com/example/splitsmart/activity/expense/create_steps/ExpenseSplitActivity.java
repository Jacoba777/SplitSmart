package com.example.splitsmart.activity.expense.create_steps;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.model.Expense;
import com.example.splitsmart.model.ExpenseSplit;
import com.example.splitsmart.model.SplitType;
import com.example.splitsmart.model.User;
import com.example.splitsmart.model.UserSession;

import java.util.ArrayList;

public class ExpenseSplitActivity extends ImprovedBaseActivity {

    ListView userListView;
    Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_split);

        userListView = findViewById(R.id.lv_members);
        expense = (Expense) getIntent().getSerializableExtra("expense");

        setListeners();
        initListViewData();
    }

    void setListeners() {
        setListener(R.id.button_next, this::onClickButtonNext);
    }

    void initListViewData() {
        userListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        ArrayList<User> members = UserSession.getGroup().members;
        ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, members);
        userListView.setAdapter(arrayAdapter);

        for(int i = 0; i < members.size(); i++) {
            userListView.setItemChecked(i, true);
        }
    }

    /**
     * Creates a new ExpenseSplit object for each checked user in the ListView.
     * @return true if at least one ExpenseSplit was creates; Else false
     */
    private boolean trySetExpenseSplitsFromCheckedUsers() {
        SparseBooleanArray includeUserBools = userListView.getCheckedItemPositions();

        expense.expenseSplits = new ArrayList<>();
        for(int i = 0; i < includeUserBools.size(); i++) {
            if(includeUserBools.valueAt(i)) {
                User user = (User) userListView.getItemAtPosition(i);
                expense.expenseSplits.add(new ExpenseSplit(user, SplitType.DEFAULT, 0));
            }
        }

        if(expense.expenseSplits.size() == 0) {
            toast("You must select at least one member to continue");
            return false;
        }
        return true;
    }

    private void onClickButtonNext() {
        if(!trySetExpenseSplitsFromCheckedUsers()) return;

        Intent intent = new Intent(this, ExpenseSplit2Activity.class);
        intent.putExtra("expense", expense);
        startActivity(intent);
        finish();
    }
}
