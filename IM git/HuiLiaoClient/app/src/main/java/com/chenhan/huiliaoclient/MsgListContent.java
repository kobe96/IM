package com.chenhan.huiliaoclient;

import android.graphics.Bitmap;

public class MsgListContent {

    private Bitmap HeadView;
    private String friendAccount;
    private String messageContent;
    private int messageCount;


    public MsgListContent(Bitmap bitmap,String friendAccount,String messageContent,int messageCount){
        this.HeadView = bitmap;
        this.friendAccount = friendAccount;
        this.messageContent = messageContent;
        this.messageCount = messageCount;
    }
    public String getMessageContent() {
        return messageContent;
    }

    public Bitmap getHeadView() {
        return HeadView;
    }

    public String getFriendAccount() {
        return friendAccount;
    }

    public int getMessageCount() {
        return messageCount;
    }

}
