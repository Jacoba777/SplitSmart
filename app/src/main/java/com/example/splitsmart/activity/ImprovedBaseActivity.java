package com.example.splitsmart.activity;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ImprovedBaseActivity extends AppCompatActivity {

    /**
     * Simple wrapper to bind a function to the onClick event of a widget.
     * @param viewId the ID of the widget to bind.
     * @param r the function to being the widget to.
     */
    public void setListener(int viewId, Runnable r) {
        findViewById(viewId).setOnClickListener(v -> r.run());
    }

    /**
     * Simplfiied wrapper to display a popup toast message to the user.
     * @param text the message to display to the user.
     */
    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
