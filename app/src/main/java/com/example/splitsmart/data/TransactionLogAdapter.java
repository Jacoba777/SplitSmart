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
import com.example.splitsmart.model.User;
import com.example.splitsmart.model.UserSession;

import java.util.ArrayList;
import java.util.Locale;

public class TransactionLogAdapter extends BaseAdapter implements ListAdapter {
    ArrayList<Expense> list;
    Context context;

    public TransactionLogAdapter(ArrayList<Expense> list, Context context) {
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

        User user = UserSession.getUser();
        Expense expense = list.get(position);

        assert expense.date != null;
        String details = String.format(Locale.US, "%s  %s  $%.2f  (You: $%.2f)",
                SQLiteManager.dateFormat.format(expense.date),
                expense.desc,
                expense.amount,
                expense.getUserShare(user));

        TextView tvDetails = view.findViewById(R.id.tvDetails);
        tvDetails.setText(details);

        //Handle buttons and add onClickListeners
        Button viewButton = view.findViewById(R.id.btn);

        viewButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExpenseViewActivity.class);
            intent.putExtra("expense", list.get(position));
            context.startActivity(intent);
        });

        return view;
    }
}
