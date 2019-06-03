package com.chenhan.huiliaoclient;

import android.graphics.Bitmap;

public class TheTruth {
    private Bitmap HeadView;
    private String Account;
    private String Date;
    private String Content;
    private int LikeCount;
    private int MessageId;
    private int isRead = 0;
    public TheTruth(Bitmap headView,String account,String date,String content,int likeCount,int messageId){
        this.HeadView = headView;
        this.Account = account;
        this.Date = date;
        this.Content = content;
        this.LikeCount = likeCount;
        this.MessageId = messageId;
    }

    public String getAccount() {
        return Account;
    }

    public Bitmap getHeadView() {
        return HeadView;
    }

    public int getLikeCount() {
        return LikeCount;
    }

    public String getContent() {
        return Content;
    }

    public String getDate() {
        return Date;
    }

    public int getMessageId(){
        return MessageId;
    }

    public void addLikeCount(){
        LikeCount++;
    }
    public int getIsRead(){
        return isRead;
    }
}
