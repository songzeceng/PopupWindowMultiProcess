package com.example.testrxjava;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button mButton;
    private Button mHide;
    private BroadcastReceiver mReceiver;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams = new WindowManager.LayoutParams();
    private MyLinearLayout mWindowView;
    private ClipboardManager mClipboardManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        TextView textView = findViewById(R.id.text_view);
        ListView listView = findViewById(R.id.list);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            list.add("No." + i + "joiaspjdsaduishdosauidqf" + (i * i - 2));
        }
        MyAdapter adapter = new MyAdapter(list, this);

        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        findViewById(R.id.button_main).setOnClickListener(v -> {
            Log.i("MainActivity", "onCreate: Button OnClick");
        });

        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);


        mWindowView = (MyLinearLayout) LayoutInflater.from(this).inflate(R.layout.window_layout, null);
        mWindowView.setActivity(this);
        mWindowManager = getWindowManager();
        mWindowLayoutParams.gravity = Gravity.START;
        mWindowLayoutParams.x = 0;
        mWindowLayoutParams.y = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mWindowLayoutParams.flags |=  WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mWindowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT;
        mWindowLayoutParams.height = 1800;
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int[] popupLocation = new int[2];
                mWindowView.getLocationOnScreen(popupLocation);
                View target = Utils.getViewTouchedByEvent(mWindowView, event);

                if (target != null) {
                    target.dispatchTouchEvent(event);
                    return true;
                }

                event.offsetLocation(popupLocation[0], popupLocation[1]);
                MainActivity.this.dispatchTouchEvent(event);
                return false;
            }
        });
        mWindowLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

        mButton = mWindowView.findViewById(R.id.btn_window);
        mButton.setOnClickListener(v -> Toast.makeText(MainActivity.this,
                "Button in window has been clicked.", Toast.LENGTH_SHORT).show());

        mHide = mWindowView.findViewById(R.id.btn_hide);
        mHide.setOnClickListener(v -> {
            mButton.setVisibility(mButton.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });
        mWindowView.findViewById(R.id.text_window).setOnLongClickListener(v -> {
            ClipData data = ClipData.newPlainText("NormalItem", ((TextView) v).getText());
            mClipboardManager.setPrimaryClip(data);
            Toast.makeText(MainActivity.this, "Content copied", Toast.LENGTH_SHORT).show();
            return true;
        });

        findViewById(R.id.root).setOnClickListener(v -> {
            Log.i("MainActivity", "Touch event gets to text view!");
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWindowManager.removeView(mWindowView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWindowManager.addView(mWindowView, mWindowLayoutParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}