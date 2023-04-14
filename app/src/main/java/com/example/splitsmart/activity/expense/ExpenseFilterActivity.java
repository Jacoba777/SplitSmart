package com.example.splitsmart.activity.expense;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.data.SQLiteManager;
import com.example.splitsmart.model.ExpenseFilter;
import com.example.splitsmart.model.ExpenseFilterBuilder;
import com.example.splitsmart.model.Group;
import com.example.splitsmart.model.User;
import com.example.splitsmart.model.UserSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ExpenseFilterActivity extends ImprovedBaseActivity {
    final Calendar myCalendar = Calendar.getInstance();
    EditText editTextAmountMin;
    EditText editTextAmountMax;
    EditText editTextDateMin;
    EditText editTextDateMax;
    EditText editTextDesc;
    Spinner spinnerPayer;
    CheckBox checkBoxRelevantOnly;
    User user;
    Group group;
    ExpenseFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_filter);

        user = UserSession.getUser();
        group = UserSession.getGroup();
        filter = (ExpenseFilter) getIntent().getSerializableExtra("filter");

        editTextAmountMin = findViewById(R.id.edittext_amount_min);
        editTextAmountMax = findViewById(R.id.edittext_amount_max);
        editTextDateMin = findViewById(R.id.edittext_date_min);
        editTextDateMax = findViewById(R.id.edittext_date_max);
        editTextDesc = findViewById(R.id.edittext_desc);
        spinnerPayer = findViewById(R.id.spinner_payer);
        checkBoxRelevantOnly = findViewById(R.id.checkbox_only_relevant_to_me);

        setWidgetsFromFilter();
        setListeners();
        setPayerSpinnerBehavior();
        setDateListeners();
    }

    void setListeners() {
        setListener(R.id.button_apply, this::onClickApply);
    }

    /**
     * Reads the filter object, and then sets the pre-populated text on all widgets from it.
     */
    void setWidgetsFromFilter() {
        editTextAmountMin.setText(filter.amountMin);
        editTextAmountMax.setText(filter.amountMax);
        editTextDateMin.setText(filter.dateMin);
        editTextDateMax.setText(filter.dateMax);
        editTextDesc.setText(filter.desc);
        spinnerPayer.setSelection(group.members.indexOf(filter.payer));
        checkBoxRelevantOnly.setChecked(filter.relevantToMeOnlyFlag);
    }

    /**
     * Invokes the ExpenseFilterBuilder to construct a new ExpenseFilter from each widget.
     * Only will build the parts of the filter that were specified by the user.
     */
    void buildFilterFromWidgets() {
        ExpenseFilterBuilder builder = new ExpenseFilterBuilder();
        builder.setGroup(group);

        String amountMin = editTextAmountMin.getText().toString();
        if(!amountMin.isEmpty()) builder.setAmountMin(amountMin);

        String amountMax = editTextAmountMax.getText().toString();
        if(!amountMax.isEmpty()) builder.setAmountMax(amountMax);

        String dateMin = editTextDateMin.getText().toString();
        if(!dateMin.isEmpty()) builder.setDateMin(dateMin);

        String dateMax = editTextDateMax.getText().toString();
        if(!dateMax.isEmpty()) builder.setDateMax(dateMax);

        String desc = editTextDesc.getText().toString();
        if(!desc.isEmpty()) builder.setDesc(desc);

        User payer = (User) spinnerPayer.getSelectedItem();
        if(payer.id > 0) builder.setPayer(payer);

        if(checkBoxRelevantOnly.isChecked()) builder.setRelevantToMeOnly(user);

        filter = builder.build();
    }

    /**
     * Sets the options for the payer filter
     */
    void setPayerSpinnerBehavior() {
        ArrayList<User> payerOptions = new ArrayList<>();
        payerOptions.add(new User(0, "[Anyone]", "", group.id)); // Dummy record so the user can clear their selection
        payerOptions.addAll(group.members);

        ArrayAdapter<User> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, payerOptions);
        spinnerPayer.setAdapter(adapter);
    }

    /**
     * Sets both date listeners' behavior
     */
    void setDateListeners() {

        DatePickerDialog.OnDateSetListener dateListenerMin = (view, year, month, day) -> {
            myCalendar.set(year, month, day);
            updateLabel(editTextDateMin);
        };

        DatePickerDialog.OnDateSetListener dateListenerMax = (view, year, month, day) -> {
            myCalendar.set(year, month, day);
            updateLabel(editTextDateMax);
        };

        editTextDateMin.setOnClickListener(view -> new DatePickerDialog(this, dateListenerMin,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH))
            .show());

        editTextDateMax.setOnClickListener(view -> new DatePickerDialog(this, dateListenerMax,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH))
                .show());
    }

    private void updateLabel(EditText editText){
        editText.setText(SQLiteManager.dateFormat.format(myCalendar.getTime()));
    }

    private void onClickApply() {
        Intent intent = new Intent();
        buildFilterFromWidgets();
        intent.putExtra("filter", filter);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
