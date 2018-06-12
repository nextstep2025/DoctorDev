package com.routetracking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.onegravity.colorpicker.ColorPickerDialog;
import com.onegravity.colorpicker.ColorPickerListener;
import com.onegravity.colorpicker.SetColorPickerListenerEvent;

public class Settings extends AppCompatActivity implements ColorPickerListener {

    private Button chooseColor;
    private View pickedColor;
    private int mBackgroundColor = 0x88000088;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();
    }

    private void init() {
        bindResources();

        colorPicked();
    }


    private void bindResources() {
        chooseColor = findViewById(R.id.choose_color);
        pickedColor = findViewById(R.id.picked_color);
        pickedColor.setBackgroundColor(ContextCompat.getColor(Settings.this,R.color.colorPrimary));
    }

    private void colorPicked() {
        chooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int initialColor = ContextCompat.getColor(Settings.this,R.color.colorPrimary);
                int dialogId = new ColorPickerDialog(Settings.this, initialColor, true).show();
                SetColorPickerListenerEvent.setListener(dialogId, Settings.this);
            }
        });
    }

    @Override
    public void onDialogClosing() {

    }

    @Override
    public void onColorChanged(int color) {
        mBackgroundColor = color;
        pickedColor.setBackgroundColor(mBackgroundColor);

        SharedPreferences sessPreferences = PreferenceManager.getDefaultSharedPreferences(Settings.this);
        SharedPreferences.Editor editor = sessPreferences.edit();
        editor.putInt("polylinecolor", mBackgroundColor).apply();
        editor.apply();



    }
}
