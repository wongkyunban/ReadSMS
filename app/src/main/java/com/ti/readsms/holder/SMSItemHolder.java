package com.ti.readsms.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.ti.readsms.R;
import com.ti.readsms.bean.SMSBean;
import com.ti.readsms.utils.DateUtils;

public class SMSItemHolder extends RecyclerView.ViewHolder {
    private TextView mTvAddress;
    private TextView mTvBody;
    private TextView mTvDate;
    public SMSItemHolder(@NonNull View itemView) {
        super(itemView);
        mTvAddress = itemView.findViewById(R.id.tv_address);
        mTvBody = itemView.findViewById(R.id.tv_body);
        mTvDate = itemView.findViewById(R.id.tv_date);
    }
    public void bind(SMSBean smsBean){
        mTvAddress.setText(smsBean.getAddress());
        mTvBody.setText(smsBean.getBody());
        mTvDate.setText(DateUtils.longToString(smsBean.getDate(),"yyyy-MM-dd"));
    }
}
