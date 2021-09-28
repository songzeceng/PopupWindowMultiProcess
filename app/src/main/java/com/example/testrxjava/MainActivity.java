package com.example.testrxjava;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button mButton;
    private Button mHide;
    private BroadcastReceiver mReceiver;
    private PopupWindow mWindow;
    private View mWindowView;
    private View mRootView;
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

        mWindow = new PopupWindow(this);
        mWindowView = LayoutInflater.from(this).inflate(R.layout.window_layout, null);

        mButton = mWindowView.findViewById(R.id.btn_window);
        mButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ProcessActivity.class));
        });

        mHide = mWindowView.findViewById(R.id.btn_hide);
        mHide.setOnClickListener(v -> {
            mButton.setVisibility(mButton.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });
        mWindowView.findViewById(R.id.text_window).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("NormalItem", ((TextView) v).getText());
                mClipboardManager.setPrimaryClip(data);
                Toast.makeText(MainActivity.this, "Content copied", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mWindow.setTouchInterceptor((view, motionEvent) -> {
            int[] popupLocation = new int[2];
            mWindowView.getLocationOnScreen(popupLocation);
            View target = Utils.getViewTouchedByEvent(mWindowView, motionEvent);

            if (target != null) {
                mWindowView.dispatchTouchEvent(motionEvent);
                return true;
            }

            motionEvent.offsetLocation(popupLocation[0], popupLocation[1]);
            MainActivity.this.dispatchTouchEvent(motionEvent);
            return false;
        });

        mRootView = getWindow().getDecorView();
        mWindow.setOutsideTouchable(false);
        mWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mWindow.setContentView(mWindowView);
        mWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(1800);

        findViewById(R.id.root).setOnClickListener(v -> {
            Log.i("MainActivity", "Touch event gets to text view!");
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            mWindow.setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//        }

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("TouchEvent")) {
                    MotionEvent event = intent.getParcelableExtra("event");
                    if (event == null) {
                        return;
                    }

                    Bundle bundle = intent.getBundleExtra("bundle");
                    if (bundle == null) {
                        return;
                    }

                    int[] subProcessArray = bundle.getIntArray("sub_process_location");

                    if (subProcessArray == null) {
                        return;
                    }

                    try {
                        Class popupClass = Class.forName("android.widget.PopupWindow");
                        Field decorViewField = popupClass.getDeclaredField("mDecorView");
                        decorViewField.setAccessible(true);
                        Object decorView = decorViewField.get(mWindow);

                        int[] popupLocation = new int[2];
                        mWindowView.getLocationOnScreen(popupLocation);
                        event.offsetLocation(subProcessArray[0] - popupLocation[0], subProcessArray[1] - popupLocation[1]);

                        Method dispatchTouchEvent = decorView.getClass().getDeclaredMethod("dispatchTouchEvent", MotionEvent.class);
                        dispatchTouchEvent.invoke(decorView, event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter("TouchEvent");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWindow.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRootView.post(() -> {
            mWindow.showAtLocation(mRootView, Gravity.START, 0, 0);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}