package com.ti.readsms.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ti.readsms.R;
import com.ti.readsms.bean.SMSBean;
import com.ti.readsms.utils.DateUtils;

public class DefaultSMSHolder extends RecyclerView.ViewHolder {
    private TextView mTvBody;
    private TextView mTvDate;
    public DefaultSMSHolder(View itemView) {
        super(itemView);
        mTvDate = (TextView)itemView.findViewById(R.id.tv_default_date);
        mTvBody = (TextView)itemView.findViewById(R.id.tv_default_body);
    }
    public void bind(SMSBean smsBean){
        mTvDate.setText(DateUtils.longToString(smsBean.getDate(),"yyyy-MM-dd"));
        mTvBody.setText(smsBean.getBody());
    }
}
