package com.internal.demo.android.utility;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.internal.demo.R;

import java.util.List;

public class MsgAdapter extends BaseAdapter
{
    private List<MsgEntity> m_dataList;
    private Context m_context;

    public MsgAdapter(Context context, List<MsgEntity> data) {
        m_context = context;
        m_dataList = data;
    }
    public void setDate(List<MsgEntity> data){
        m_dataList = data;
    }
    @Override
    public int getCount() {
        return m_dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return m_dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        ViewHolder holder = null;
        if(null == view) {
            view = LayoutInflater.from(m_context).inflate(R.layout.msg_item, null);
            holder = new ViewHolder();
            holder.accountId = (TextView) view.findViewById(R.id.tv_item_account);
            holder.msgText = (TextView) view.findViewById(R.id.tv_item_msg);
            holder.result = (TextView) view.findViewById(R.id.tv_item_result);
            holder.state = (TextView) view.findViewById(R.id.tv_item_state);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        MsgEntity msgEntity = (MsgEntity)m_dataList.get(i);

        holder.accountId.setText(msgEntity.getMsgAccount()+":");

        holder.msgText.setText(msgEntity.getMsgText());

        holder.result.setText(String.valueOf(msgEntity.getMsgSendState()));

        if(msgEntity.getMsgPeerState() == 0)
        {
            holder.state.setText("已发送");
        }
        else if(msgEntity.getMsgPeerState() == 1)
        {
            holder.state.setText("已送达");
        }
        else if(msgEntity.getMsgPeerState() == 2)
        {
            holder.state.setText("已读");
        }

        return view;
    }

    public class ViewHolder
    {
        private TextView accountId;
        private TextView msgText;
        private TextView result;
        private TextView state;
    }
}
