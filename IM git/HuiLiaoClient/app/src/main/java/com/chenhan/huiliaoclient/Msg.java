package com.chenhan.huiliaoclient;

import android.graphics.Bitmap;

import java.util.Date;

public class Msg{
    //收到一条消息
    public static final int RECEIVED = 0;

    //发出一条消息
    public static final int SENT = 1;

    //消息的内容
    private String  content;

    //消息发送者头像
    private Bitmap headView;

    //消息的类型
    private int type;

    public  Msg(String content,int type,Bitmap headView){
        this.content = content;
        this.type = type;
        this.headView = headView;
    }

    public String getContent(){
        return content;
    }

    public int getType(){
        return type;
    }

    public Bitmap getHeadView() {
        return headView;
    }
}
