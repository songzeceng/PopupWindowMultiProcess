package com.example.testrxjava;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ProcessActivity extends Activity {
    public static final String TAG = "ProcessActivity";
    private View mRootView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = LayoutInflater.from(this).inflate(R.layout.activity_process, null);
        setContentView(mRootView);
        Log.i(TAG, "onCreate: pid = " + Process.myPid());

        Button button_process = findViewById(R.id.btn_process);
        TextView button_finish = findViewById(R.id.btn_finish);

        button_process.setOnClickListener(v -> {
            Toast.makeText(this, "onCreate: Button in sub process has been clicked.", Toast.LENGTH_SHORT).show();
        });
        button_finish.setOnClickListener(v -> {
            ProcessActivity.this.finish();
        });

        ToggleButton toggleButton = findViewById(R.id.toggle);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> Toast.makeText(ProcessActivity.this,
                "Toggle button in sub process has been clicked, current state of checking is: " + isChecked,
                Toast.LENGTH_SHORT).show());

        Switch switch_button = findViewById(R.id.switch_sub_process);
        switch_button.setOnCheckedChangeListener((buttonView, isChecked) -> Toast.makeText(ProcessActivity.this,
                "Switch in sub process has been clicked, current state of checking is: " + isChecked,
                Toast.LENGTH_SHORT).show());

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = 950;
        lp.height = 1700;
        lp.gravity = Gravity.START;
        getWindow().setAttributes(lp);

        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View target = Utils.getViewTouchedByEvent(mRootView, event);
                if (target != null) {
                    target.dispatchTouchEvent(event);
                    return true;
                }

                Intent intent = new Intent();
                intent.setAction("TouchEvent");
                intent.putExtra("event", event);

                Bundle bundle = new Bundle();
                int[] subProcessStartLocation = new int[2];
                getWindow().getDecorView().getLocationOnScreen(subProcessStartLocation);
                bundle.putIntArray("sub_process_location", subProcessStartLocation);
                intent.putExtra("bundle", bundle);

                sendBroadcast(intent);
                return false;
            }
        });
    }
}
