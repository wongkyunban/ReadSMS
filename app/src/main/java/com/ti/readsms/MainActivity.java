package com.ti.readsms;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;


import com.ti.readsms.adapter.SmsAdapter;
import com.ti.readsms.bean.SMSBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRvSms;
    private List<SMSBean> list = Collections.synchronizedList(new ArrayList<SMSBean>());
    private SmsAdapter adapter;
    private Paint mPaint = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRvSms = (RecyclerView) findViewById(R.id.rv_sms);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRvSms.setLayoutManager(linearLayoutManager);
        mPaint.setAntiAlias(true);
        mPaint.setARGB(238, 238, 238, 238);
        mRvSms.addItemDecoration(new RecyclerView.ItemDecoration(){
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                // RecyclerView中的item数量
                int childCount = parent.getChildCount();
                // 遍历子view
                for (int i = 0; i < childCount; i++) {
                    View view = parent.getChildAt(i);
                    // 在adapter显示的对应位置
                    int index = parent.getChildAdapterPosition(view);
                    //第一个ItemView不需要绘制
                    if (index == 0) {
                        continue;
                    }
                    // 分界线 left
                    float dividerLeft = parent.getPaddingLeft() + 20;
                    // 分界线 top
                    float dividerTop = view.getTop() - 2;
                    // 分界线 right
                    float dividerRight = parent.getWidth() - parent.getPaddingRight() - 20;
                    // 分界线 bottom
                    float dividerBottom = view.getTop();
                    c.drawRect(dividerLeft, dividerTop, dividerRight, dividerBottom, mPaint);
                }
            }
        });
        adapter = new SmsAdapter();
        mRvSms.setAdapter(adapter);
        adapter.setSmsInterfaceListener(new SmsAdapter.SmsInterface() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,SmsDetailActivity.class);
                intent.putExtra("thread_id",list.get(position).getThread_id());
                intent.putExtra("address",list.get(position).getAddress());
                startActivity(intent);
            }
        });
        acquirePermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        readSMS();
    }

    private void readSMS() {
        list.clear();
        new ReadSMSThread(this).start();

    }


    private UpdateUIHandler handler = new UpdateUIHandler(this);
    private static final int EVENT_SMS = 100;

    private static class UpdateUIHandler extends Handler {
        private WeakReference<MainActivity> activityWeakReference;

        public UpdateUIHandler(MainActivity mainActivity) {
            this.activityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_SMS:
                    activityWeakReference.get().adapter.setList(activityWeakReference.get().list);
                    activityWeakReference.get().adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private static class ReadSMSThread extends Thread {
        private WeakReference<MainActivity> activityWeakReference;

        public ReadSMSThread(MainActivity mainActivity) {
            this.activityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void run() {
            Uri smsUri = Uri.parse("content://sms/");
            ContentResolver cr = activityWeakReference.get().getContentResolver();
            String[] projectionOne = new String[]{"DISTINCT thread_id"};
            Cursor cursorOne = cr.query(smsUri, projectionOne, null, null, "date desc");
            if (null == cursorOne) {
                return;
            }
            while (cursorOne.moveToNext()) {
                String thread_id = cursorOne.getString(cursorOne.getColumnIndex("thread_id"));
                String[] projection = new String[]{"_id", "thread_id", "address", "person", "date", "date_sent", "read", "status", "body", "protocol", "type"};

                Cursor cursor = cr.query(smsUri, projection, "thread_id=?", new String[]{thread_id}, "date desc");
                if (null == cursor) {
                    return;
                }
                cursor.moveToNext();
                SMSBean smsBean = new SMSBean();
                smsBean.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                smsBean.setThread_id(cursor.getString(cursor.getColumnIndex("thread_id")));
                smsBean.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                smsBean.setPerson(cursor.getString(cursor.getColumnIndex("person")));
                smsBean.setDate(cursor.getLong(cursor.getColumnIndex("date")));
                smsBean.setDate_sent(cursor.getLong(cursor.getColumnIndex("date_sent")));
                smsBean.setRead(cursor.getInt(cursor.getColumnIndex("read")));
                smsBean.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                smsBean.setBody(cursor.getString(cursor.getColumnIndex("body")));
                smsBean.setProtocol(cursor.getInt(cursor.getColumnIndex("protocol")));
                smsBean.setType(cursor.getInt(cursor.getColumnIndex("type")));
                activityWeakReference.get().list.add(smsBean);
                smsBean = null;
                cursor.close();


            }
            cursorOne.close();


            Message msg = new Message();
            msg.what = EVENT_SMS;
            activityWeakReference.get().handler.sendMessage(msg);


        }
    }

    private final static int REQUEST_CODE_PERMISSION = 1;

    // 权限
    private void acquirePermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
            ||checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
            ||checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS,Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(MainActivity.this, "权限申请失败", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (list != null) {
            list.clear();
            list = null;
        }
        if (adapter != null) {
            adapter.destroy();
        }
        super.onDestroy();
    }
}
