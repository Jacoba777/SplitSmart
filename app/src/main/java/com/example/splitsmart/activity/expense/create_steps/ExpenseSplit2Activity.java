package com.example.splitsmart.activity.expense.create_steps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.activity.expense.receipt.ExpenseCreateReceiptActivity;
import com.example.splitsmart.model.Expense;
import com.example.splitsmart.model.ExpenseSplit;
import com.example.splitsmart.model.SplitType;

import java.util.HashMap;
import java.util.Locale;

public class ExpenseSplit2Activity extends ImprovedBaseActivity {
    Expense expense;
    ExpenseSplit expenseSplitActive;
    Spinner spinnerUser;
    Spinner spinnerSplitType;
    SwitchCompat switchUseCustomAmount;
    EditText editTextCustomAmount;
    TextView summary;
    HashMap<String, SplitType> splitTypeMap;
    String[] splitTypeLabels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_split2);

        expense = (Expense) getIntent().getSerializableExtra("expense");
        expenseSplitActive = expense.expenseSplits.get(0);

        spinnerUser = findViewById(R.id.spinner);
        spinnerSplitType = findViewById(R.id.spinner_amount_type);
        switchUseCustomAmount = findViewById(R.id.switch_use_custom_amount);
        editTextCustomAmount = findViewById(R.id.edittext_custom_amount);
        summary = findViewById(R.id.tv_summary);

        splitTypeMap = new HashMap<>();
        splitTypeMap.put("Fixed Amount", SplitType.FIXED_AMOUNT);
        splitTypeMap.put("Percentage", SplitType.PERCENTAGE);
        splitTypeLabels = splitTypeMap.keySet().toArray(new String[]{});

        setSwitchUseCustomAmountBehavior();
        setListeners();
        setUserSpinnerBehavior();
        setSplitTypeSpinnerItems();
        updateSplitSummaryText();
    }

    private void setListeners() {
        setListener(R.id.button_next, this::onClickButtonNext);
        setListener(R.id.button_update, this::onClickUpdate);
    }

    /**
     * Toggles showing/hiding the custom amount layout.
     * Additionally, if toggled to not use a custom amount, it triggers an update to recalculate the splits.
     */
    private void setSwitchUseCustomAmountBehavior() {
        RelativeLayout customAmountLayout = findViewById(R.id.layout_custom_amount);

        switchUseCustomAmount.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) {
                customAmountLayout.setVisibility(View.VISIBLE);
            } else {
                customAmountLayout.setVisibility(View.GONE);
                onClickUpdate();
            }
        });
    }

    /**
     * Sets the behavior for the User selection spinner (its items, and what to do when the user selects a particular item).
     */
    private void setUserSpinnerBehavior() {
        spinnerUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                expenseSplitActive = expense.expenseSplits.get(i);

                spinnerSplitType.setSelection(expenseSplitActive.splitType.ordinal());
                switchUseCustomAmount.setChecked(expenseSplitActive.splitType != SplitType.DEFAULT);
                editTextCustomAmount.setText(String.format(Locale.US, "%.2f", (expenseSplitActive.splitType == SplitType.FIXED_AMOUNT ?
                        expenseSplitActive.amount :
                        expenseSplitActive.percentage)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        ArrayAdapter<ExpenseSplit> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expense.expenseSplits);
        spinnerUser.setAdapter(arrayAdapter);
    }

    /**
     * Sets the items of the Split Type spinner.
     */
    private void setSplitTypeSpinnerItems() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, splitTypeLabels);
        spinnerSplitType.setAdapter(arrayAdapter);
    }

    private void updateSplitSummaryText() {
        StringBuilder sb = new StringBuilder();
        boolean valid = expense.validateSplits();
        for(ExpenseSplit es : expense.expenseSplits) {
            sb.append(String.format(Locale.US, "%s: $%.2f (%.2f%%)\n", es.user, es.amount, es.percentage));
        }
        sb.append(String.format("This split is currently %s.", (valid ? "valid" : "invalid")));
        summary.setText(sb.toString());
    }

    /**
     * Sets the values for the active ExpenseSplit based on the values input by the user.
     * Triggers an update on the summary text once finished.
     */
    private void updateActiveSplitFromWidgets() {
        boolean useCustomAmount = switchUseCustomAmount.isChecked();
        String selectedSplitTypeLabel = (String) spinnerSplitType.getSelectedItem();
        float newAmountOrPercentage = tryParseFloat(editTextCustomAmount.getText().toString());

        expenseSplitActive.splitType = (useCustomAmount ? splitTypeMap.get(selectedSplitTypeLabel) : SplitType.DEFAULT);
        expenseSplitActive.amount = newAmountOrPercentage;
        expenseSplitActive.percentage = newAmountOrPercentage;

        updateSplitSummaryText();
    }

    /**
     * Attempts to parse a string to a float.
     * @param str The string to be parsed.
     * @return the parsed float, or 0 if parsing failed.
     */
    private float tryParseFloat(String str) {
        try {
            return Float.parseFloat(str);
        } catch(NumberFormatException ex) {
            return 0f;
        }
    }

    /**
     * If the current split arrangement is valid, insert it into the database.
     * @return true if the splits were inserted; Else false.
     */
    private boolean insertSplitsIfValid() {
        if(!expense.validateSplits()) {
            toast("This split arrangement does not sum up to 100%.");
            return false;
        }

        expense.insertOrUpdate(this);
        return true;
    }

    private void onClickUpdate() {
        updateActiveSplitFromWidgets();
    }

    private void onClickButtonNext() {
        if (!insertSplitsIfValid()) return;

        Intent intent = new Intent(this, ExpenseCreateReceiptActivity.class);
        intent.putExtra("expense", expense);
        startActivity(intent);
        finish();
    }
}
