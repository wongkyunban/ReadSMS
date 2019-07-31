package com.ti.readsms;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ti.readsms.adapter.SmsDetailAdapter;
import com.ti.readsms.bean.SMSBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmsDetailActivity extends AppCompatActivity {

    private RecyclerView mRvDetail;
    private SmsDetailAdapter adapter;
    private String thread_id;
    private String number;
    private List<SMSBean> list = Collections.synchronizedList(new ArrayList<SMSBean>());
    private EditText mContentText;
    private Button mSendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_detail);
        mRvDetail = (RecyclerView) findViewById(R.id.rv_content);
        mContentText = (EditText) findViewById(R.id.et_send_content);
        mSendBtn = (Button)findViewById(R.id.btn_send);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSms();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRvDetail.setLayoutManager(linearLayoutManager);
        adapter = new SmsDetailAdapter();
        mRvDetail.setAdapter(adapter);
        thread_id = getIntent().getStringExtra("thread_id");
        number = getIntent().getStringExtra("address");
        initSms(thread_id);

    }

    private void initSms(String threadId) {
        if (!TextUtils.isEmpty(threadId)) {
            list.clear();
            new ReadSMSDetailThread(this, thread_id).start();
        }
    }

    // 发短信
    private void sendSms() {
        String content = mContentText.getText().toString();
        if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(number)) {
            ArrayList<String> messages = SmsManager.getDefault().divideMessage(content);
            for (String text : messages) {
                SmsManager.getDefault().sendTextMessage(number, null, text, null, null);
            }
        }
    }

    private static class ReadSMSDetailThread extends Thread {
        private WeakReference<SmsDetailActivity> activityWeakReference;

        private WeakReference<String> thread_id;

        public ReadSMSDetailThread(SmsDetailActivity activity, String thread_id) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.thread_id = new WeakReference<>(thread_id);
        }

        @Override
        public void run() {
            Uri smsUri = Uri.parse("content://sms/");
            ContentResolver cr = activityWeakReference.get().getContentResolver();
            String[] projection = new String[]{"_id", "thread_id", "address", "person", "date", "date_sent", "read", "status", "body", "protocol", "type"};
            Cursor cursor = cr.query(smsUri, projection, "thread_id=?", new String[]{thread_id.get()}, "date asc");
            if (null == cursor) {
                return;
            }
            while (cursor.moveToNext()) {
                String thread_id = cursor.getString(cursor.getColumnIndex("thread_id"));


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


            }
            cursor.close();


            Message msg = new Message();
            msg.what = EVENT_SMS;
            activityWeakReference.get().handler.sendMessage(msg);

        }
    }

    private SmsDetailUIHandler handler = new SmsDetailUIHandler(this);
    private static final int EVENT_SMS = 100;

    private static class SmsDetailUIHandler extends Handler {
        private WeakReference<SmsDetailActivity> activityWeakReference;

        public SmsDetailUIHandler(SmsDetailActivity mActivity) {
            this.activityWeakReference = new WeakReference<>(mActivity);
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

}
