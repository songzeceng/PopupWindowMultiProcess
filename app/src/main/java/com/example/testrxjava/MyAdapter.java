package com.example.testrxjava;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends BaseAdapter {
    private List<String> mList;
    private Context mContext;
    private ClipboardManager mClipboardManager;

    public MyAdapter(List<String> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        mClipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public String getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_layout, null);
        }
        TextView textItem = view.findViewById(R.id.text_item);
        textItem.setText(getItem(i));
        textItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("NormalItem", textItem.getText());
                mClipboardManager.setPrimaryClip(data);
                Toast.makeText(mContext, "Content copied", Toast.LENGTH_SHORT).show();
//                ToastUtil.centerToast(R.string.ttlive_perftools_item_copied);
                return false;
            }
        });
        return view;
    }
}
