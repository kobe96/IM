package com.chenhan.huiliaoclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MsgAdapter extends ArrayAdapter<Msg> {

    private int resourceId;

    public MsgAdapter(Context context, int textViewResourceId, List<Msg> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Msg msg = getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftHeadView = (CircleImageView)view.findViewById(R.id.Message_List_Left_HeadView);
            viewHolder.rightHeadView = (CircleImageView)view.findViewById(R.id.Message_List_Right_HeadView);
            viewHolder.leftLayout = (LinearLayout) view.findViewById(R.id.Message_List_Left_Layout);
            viewHolder.rightLayout = (LinearLayout)view.findViewById(R.id.Message_List_Right_Layout);
            viewHolder.leftMsg = (TextView)view.findViewById(R.id.Message_List_Left_Msg);
            viewHolder.rightMsg = (TextView)view.findViewById(R.id.Message_List_Right_Msg);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        if(msg.getType()==Msg.RECEIVED){
            //如果是收到的消息，则显示左边消息布局，将右边消息布局隐藏
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(msg.getContent());
            viewHolder.leftHeadView.setImageBitmap(msg.getHeadView());
        }else if(msg.getType()==Msg.SENT){
            //如果是发出去的消息，显示右边布局的消息布局，将左边的消息布局隐藏
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightMsg.setText(msg.getContent());
            viewHolder.rightHeadView.setImageBitmap(msg.getHeadView());
        }
        return view;
    }

    class ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        CircleImageView leftHeadView;
        CircleImageView rightHeadView;
    }
}
