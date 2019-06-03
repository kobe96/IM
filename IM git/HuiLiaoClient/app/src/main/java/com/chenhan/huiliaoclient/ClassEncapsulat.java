package com.chenhan.huiliaoclient;

import android.provider.ContactsContract;

public class ClassEncapsulat {

}

class MessageTable{
    private int MessageId;	                                                                     //消息ID
    private String MessageContent;	                                                             //消息的内容
    private String SendAccount;                                                                   //发送消息账号
    private String ReceiveAccount;                                                                //接收消息的ID
    private int MessageType;                                                                      //消息类型
    private int MessageReceiveType = 0;                                                          //消息是否被查看

    MessageTable(int MessageId,String MessageContent,String SendAccount,String ReceiveAccount,int MessageType,int MessageReceiveType){
        this.MessageId = MessageId;
        this.MessageContent = MessageContent;
        this.SendAccount = SendAccount;
        this.ReceiveAccount = ReceiveAccount;
        this.MessageType = MessageType;
        this.MessageReceiveType = MessageReceiveType;
    }
    MessageTable(){}

    public int getMessageId() {
        return MessageId;
    }

    public String getMessageContent() {
        return MessageContent;
    }

    public String getSendAccount() {
        return SendAccount;
    }

    public String getReceiveAccount() {
        return ReceiveAccount;
    }

    public int getMessageType() {
        return MessageType;
    }

    public int getMessageReceiveType(){
        return MessageReceiveType;
    }

    public void setMessageId(int messageId) {
        MessageId = messageId;
    }

    public void setMessageContent(String messageContent) {
        MessageContent = messageContent;
    }

    public void setSendAccount(String sendAccount) {
        SendAccount = sendAccount;
    }

    public void setReceiveAccount(String receiveAccount) {
        ReceiveAccount = receiveAccount;
    }

    public void setMessageType(int messageType) {
        MessageType = messageType;
    }

    public void setMessageReceiveType(int MessageReceiveType){
        this.MessageReceiveType = MessageReceiveType;
    }

}


class FriendListTable{
    private int FriendId;                                                                         //朋友的ID
    private String MyAccount;                                                                     //我的账号
    private String FriendAccount;	                                                                //朋友的账号
    private String FriendName;		                                                                    //朋友的姓名
    FriendListTable(){}
    FriendListTable (String MyAccount,int FriendId,String Account,String FriendName){
        this.MyAccount = MyAccount;
        this.FriendId = FriendId;
        this.FriendAccount = Account;
        this.FriendName = FriendName;
    }

    public int getFriendId() {
        return FriendId;
    }

    public String getFriendAccount() {
        return FriendAccount;
    }

    public String getFriendName() {
        return FriendName;
    }

    public String getMyAccount() {
        return MyAccount;
    }

    public void setFriendAccount(String friendAccount) {
        FriendAccount = friendAccount;
    }

    public void setFriendId(int friendId) {
        FriendId = friendId;
    }

    public void setFriendName(String friendName) {
        FriendName = friendName;
    }

    public void setMyAccount(String myAccount) {
        MyAccount = myAccount;
    }
}

class MySettingsTable{
    private int HeadId;
    private String Account;
    private String NikeName;
    private int Age;
    private String Address;
    private String PhoneNum;
    private int Sex;
    public MySettingsTable(){}

    public MySettingsTable(int HeadId,String Account,String NikeName,int Age,String Address,String PhoneNum,int sex ){
        this.HeadId = HeadId;
        this.Account = Account;
        this.NikeName = NikeName;
        this.Age = Age;
        this.Address = Address;
        this.PhoneNum = PhoneNum;
        this.Sex = sex;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setAge(int age) {
        Age = age;
    }

    public void setHeadId(int headId) {
        HeadId = headId;
    }

    public void setNikeName(String nikeName) {
        NikeName = nikeName;
    }

    public void setPhoneNum(String phoneNum) {
        PhoneNum = phoneNum;
    }

    public void setSex(int sex) {
        Sex = sex;
    }


    public String getAccount() {
        return Account;
    }

    public int getHeadId() {
        return HeadId;
    }

    public String getNikeName() {
        return NikeName;
    }

    public int getAge() {
        return Age;
    }

    public String getAddress() {
        return Address;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public int getSex() {
        return Sex;
    }
}

class HeadViewTable {
    private byte [] HeadView;
    private String Account;

    public HeadViewTable(byte[] headView, String account){
        this.HeadView = headView;
        this.Account = account;
    }
    public byte[] getHeadView() {
        return HeadView;
    }

    public String getAccount(){
        return Account;
    }


}