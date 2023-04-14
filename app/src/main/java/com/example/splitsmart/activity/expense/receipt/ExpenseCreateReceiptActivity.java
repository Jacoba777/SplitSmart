package com.example.splitsmart.activity.expense.receipt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.splitsmart.R;
import com.example.splitsmart.activity.ImprovedBaseActivity;
import com.example.splitsmart.model.Expense;
import com.example.splitsmart.model.ImageReceipt;

public class ExpenseCreateReceiptActivity extends ImprovedBaseActivity {
    ActivityResultLauncher<Intent> resultLauncher;
    ImageView imageViewReceipt;
    ImageReceipt imageReceipt;
    Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_receipt_create);

        expense = (Expense) getIntent().getSerializableExtra("expense");
        imageViewReceipt = findViewById(R.id.img_receipt);
        imageReceipt = null;

        setListeners();
        setImageUploadCallbackListener();
    }

    void setListeners() {
        setListener(R.id.button_receipt, this::onClickReceipt);
        setListener(R.id.button_next, this::onClickButtonNext);
    }

    /**
     * After an image has been uploaded, display it on the screen and store it in local memory.
     */
    void setImageUploadCallbackListener() {
        resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        Intent data = result.getData();
                        if(data == null) return;
                        Uri imageUri = data.getData();
                        ImageDecoder.Source imageSource = ImageDecoder.createSource(this.getContentResolver(), imageUri);
                        Bitmap bitmap = ImageDecoder.decodeBitmap(imageSource);
                        imageViewReceipt.setImageBitmap(bitmap);
                        imageReceipt = new ImageReceipt(bitmap);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                });
    }

    /**
     * Triggers Android's built-in image upload dialog
     */
    private void onClickReceipt()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        resultLauncher.launch(intent);
    }

    private void onClickButtonNext() {
        if(imageReceipt != null) imageReceipt.updateExpenseReceipt(this, expense);
        finish();
    }
}
