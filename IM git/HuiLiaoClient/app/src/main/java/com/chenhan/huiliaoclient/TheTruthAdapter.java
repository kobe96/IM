package com.chenhan.huiliaoclient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.WebSocket;

public class TheTruthAdapter extends ArrayAdapter<TheTruth> {
    private int resourceId;
    private TheTruth[] theTruths;
    public TheTruthAdapter(Context context, int textViewResourceId, List<TheTruth> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final int isPosition = position;
        theTruths = FragmentTheTruth.theTruths;
        final TheTruth theTruth = getItem(position);
        View view;
        final TheTruthAdapter.ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.headView = (CircleImageView)view.findViewById(R.id.Find_The_Truth_HeadView);
            viewHolder.account = (TextView) view.findViewById(R.id.Find_The_Truth_Account);
            viewHolder.dateTime = (TextView) view.findViewById(R.id.Find_The_Truth_Date);
            viewHolder.content = (TextView) view.findViewById(R.id.Find_The_Truth_Content_TextView);
            viewHolder.likeCount = (TextView)view.findViewById(R.id.Find_The_Truth_Like_Count_TextView);
            viewHolder.likeBtn = (ImageView)view.findViewById(R.id.Find_The_Truth_Like_ImageView);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (TheTruthAdapter.ViewHolder) view.getTag();
        }

        viewHolder.headView.setImageBitmap(theTruth.getHeadView());
        viewHolder.account.setText(theTruth.getAccount());
        viewHolder.dateTime.setText(theTruth.getDate());
        viewHolder.content.setText(theTruth.getContent());
        viewHolder.likeCount.setText(String.valueOf(theTruths[isPosition].getLikeCount()));

        viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("account", theTruths[isPosition].getAccount());
                    jsonObject.put("myAccount", MainActivity.account);
                    jsonObject.put("messageId", theTruths[isPosition].getMessageId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MainActivity.listener.sendLikes(jsonObject.toString());
                theTruths[isPosition].addLikeCount();
                String text = String.valueOf(theTruths[isPosition].getLikeCount());
                viewHolder.likeCount.setText(text);
            }
        });

        return view;
    }

    class ViewHolder{
        CircleImageView headView;//头像
        TextView account;
        TextView dateTime;
        TextView content;
        TextView likeCount;
        ImageView likeBtn;

    }
}

