package com.ti.readsms.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.ti.readsms.R;
import com.ti.readsms.bean.SMSBean;
import com.ti.readsms.holder.SMSItemHolder;

import java.util.ArrayList;
import java.util.List;

public class SmsAdapter extends RecyclerView.Adapter {
    private List<SMSBean> list = new ArrayList<>();
    private SmsInterface smsInterfaceListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_itme_holder, parent, false);
        return new SMSItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        ((SMSItemHolder) holder).bind(list.get(position));
        ((SMSItemHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(smsInterfaceListener != null){
                    smsInterfaceListener.onClick(v,position);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<SMSBean> list) {
        this.list = list;
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

    public void setSmsInterfaceListener(SmsInterface smsInterfaceListener) {
        this.smsInterfaceListener = smsInterfaceListener;
    }

    public interface SmsInterface{
        void onClick(View view,int position);
    }
}
