package com.example.splitsmart.data;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.expense.ExpenseViewActivity;
import com.example.splitsmart.model.Expense;
import com.example.splitsmart.model.Group;
import com.example.splitsmart.model.User;
import com.example.splitsmart.model.UserSession;

import java.util.ArrayList;
import java.util.Locale;

public class RemoveMembersAdapter extends BaseAdapter implements ListAdapter {
    ArrayList<User> list;
    Context context;

    public RemoveMembersAdapter(ArrayList<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.resource_transaction_display, null);
        }

        Group group = UserSession.getGroup();
        User loggedInUser = UserSession.getUser();
        User userToRemove = list.get(position);

        TextView tvDetails = view.findViewById(R.id.tvDetails);
        tvDetails.setText(userToRemove.username);

        //Handle buttons and add onClickListeners
        Button viewButton = view.findViewById(R.id.btn);
        viewButton.setText("Remove");

        viewButton.setOnClickListener(v -> {
            userToRemove.groupid = 0;
            userToRemove.update(context);

            group.members.removeIf(u -> u.id == userToRemove.id);
            UserSession.login(loggedInUser, group);

            list.remove(position);
            this.notifyDataSetChanged();
        });

        return view;
    }
}
