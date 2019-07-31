package com.ti.readsms.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ti.readsms.R;
import com.ti.readsms.bean.SMSBean;
import com.ti.readsms.holder.DefaultSMSHolder;
import com.ti.readsms.holder.ReceivedSMSHolder;
import com.ti.readsms.holder.SendedSMSHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmsDetailAdapter extends RecyclerView.Adapter {
    private List<SMSBean> list = Collections.synchronizedList(new ArrayList<SMSBean>());

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 短信类型1是接收到的，2是已发出
        switch (viewType) {
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_sms_item, parent, false);
                return new ReceivedSMSHolder(view1);
            case 2:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.sended_sms_item, parent, false);
                return new SendedSMSHolder(view2);
            default:
                View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.default_sms_item, parent, false);
                return new DefaultSMSHolder(view3);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (list.get(position).getType()){
            case 1:
                ((ReceivedSMSHolder)holder).bind(list.get(position));
                break;
            case 2:
                ((SendedSMSHolder)holder).bind(list.get(position));
                break;
            default:
                ((DefaultSMSHolder)holder).bind(list.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();

    }

    public void setList(List<SMSBean> list) {
        this.list = list;
    }

    public void appendList(List<SMSBean> list) {
        if (list != null) {
            this.list.addAll(list);
        }
    }
    // return position
    public int appendList(SMSBean smsBean){
        if(list != null){
            if(list.indexOf(smsBean) == -1){
                int position = list.size();
                list.add(position,smsBean);
                return position;
            }else{
                return -1;
            }

        }else {
            return -1;
        }
    }

    public void clear() {
        if (list != null) {
            list.clear();
        }
    }

    public void destroy() {
        if (list != null) {
            list.clear();
            list = null;
        }
    }
}
