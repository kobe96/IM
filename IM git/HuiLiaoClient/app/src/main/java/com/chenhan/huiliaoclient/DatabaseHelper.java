package com.chenhan.huiliaoclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    @Override
    //软件版本号改变时调用
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static final String DB_NAME = "HuiChat.db";
    //数据库版本                                        //数据库名称
    private static final int version = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, version);
    }


    public void onCreate(SQLiteDatabase sQLiteDatabase) {

/*****************************************建立消息表***********************************************/

        String createMessageTable = "create table MessageTable (" +
                "MessageId            int not null," +                                             //Message消息ID
                "MessageContent       varchar(1000)," +                                             //Message内容
                "SendAccount          varchar(20)," +                                               //发送者账号
                "ReceiveAccount       varchar(20)," +                                               //接收者账号
//                "MessageTime          varchar(20)," +                                              //接收时间
                "MessageType          int not null," +                                             //消息类型
                "MessageReceiveType   int not null," +                                             //消息是否被读取
                "primary key (MessageId) );";
        sQLiteDatabase.execSQL(createMessageTable);

/*****************************************建立朋友表***********************************************/

        String createFriendListTable = "create table FriendListTable (" +
                "FriendId             int        ," +                                               //朋友的ID
                "MyAccount            varchar(20)," +                                               //我的账号
                "FriendAccount        varchar(20)," +                                               //朋友的账号
                "FriendName           varchar(20)," +                                               //朋友的姓名
                "primary key (FriendId));";
        sQLiteDatabase.execSQL(createFriendListTable);


        /*****************************************个人设置***********************************************/
        String createMySettingsTable = "create table MySettingsTable (" +
                "HeadId             int        ," +                                                 //头像编号
                "Account            varchar(20)," +                                                 //我的账号
                "NikeName           varchar(20)," +                                                 //昵称
                "Age                int        ," +                                                 //年龄
                "Address            varchar(80)," +                                                 //地址
                "PhoneNum           varchar(11)," +                                          //电话
                "Sex                int        ," +                                                 //性别
                "primary key (Account));";
        sQLiteDatabase.execSQL(createMySettingsTable);

        /*****************************************头像储存***********************************************/
        String createHeadViewTable = "create table HeadViewTable (" +
                "HeadView             blob     ," +                                             //头像
                "Account            varchar(20)," +                                             //我的账号
                "primary key (Account));";
        sQLiteDatabase.execSQL(createHeadViewTable);
    }
}
