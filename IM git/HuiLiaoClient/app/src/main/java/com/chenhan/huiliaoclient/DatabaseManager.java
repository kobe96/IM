package com.chenhan.huiliaoclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class DatabaseManager {
    public static DatabaseHelper databaseHelp;
    public DatabaseManager(Context context) {
        databaseHelp = new DatabaseHelper(context);//继承自带的SQLiteOpenHelper类，建数据库
    }
    public static int busyFlag = 0;


    //    插入一条消息
    synchronized public long insertMessage(MessageTable messageTable){
//        以读写方式打开
        SQLiteDatabase sqLiteDatabase = databaseHelp.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.clear();
        contentValues.put("MessageId",messageTable.getMessageId());
        contentValues.put("MessageContent",messageTable.getMessageContent());
        contentValues.put("SendAccount",messageTable.getSendAccount());
        contentValues.put("ReceiveAccount",messageTable.getReceiveAccount());
        contentValues.put("MessageType",messageTable.getMessageType());
        contentValues.put("MessageReceiveType",messageTable.getMessageReceiveType());
        long result  = sqLiteDatabase.insert("MessageTable", null, contentValues);
        sqLiteDatabase.close();
        return result;
    }

    //查询和指定朋友的聊天内容
    synchronized public MessageTable [] queryMessage(String myAccount,String friendAccount){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query(false,"MessageTable",new String[]{"MessageId","MessageContent","SendAccount","ReceiveAccount","MessageType","MessageReceiveType"},
                " ( SendAccount = " + "'" + myAccount + "'" + " and "+ "MessageType =" + " '" + 1 + "'"+  " and "+ "ReceiveAccount =" + " '" + friendAccount + "' ) " +
                        "or" + " ( SendAccount =" +" '" + friendAccount + "' and " + " ReceiveAccount =" + " '" + myAccount + "' and "+ "MessageType = "+ "'"+ 0 +"'"+ " )" ,null,null,null,
                "MessageId asc",null);//查询并获得游标
        MessageTable [] messageTables = new MessageTable[cursor.getCount()];
        for (int i=0; cursor.moveToNext() == true; i++ ){
            messageTables[i] = new MessageTable(
                    cursor.getInt(cursor.getColumnIndex("MessageId")),
                    cursor.getString(cursor.getColumnIndex("MessageContent")),
                    cursor.getString(cursor.getColumnIndex("SendAccount")),
                    cursor.getString(cursor.getColumnIndex("ReceiveAccount")),
                    cursor.getInt(cursor.getColumnIndex("MessageType")),
                    cursor.getInt(cursor.getColumnIndex("MessageReceiveType"))
                    );
        }
        sQLiteDatabase.close();
        return messageTables;
    }

    //返回与指定好友账户的消息数量
    synchronized public int queryMessageNum(String myAccount,String friendAccount){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query(false,"MessageTable",new String[]{"MessageId","MessageContent","SendAccount","ReceiveAccount","MessageType"},
                " ( SendAccount = " + "'" + myAccount + "'" + " and "+ "MessageType =" + " '" + 1 + "'"+  " and "+ "ReceiveAccount =" + " '" + friendAccount + "' ) " +
                        "or" + " ( SendAccount =" +" '" + friendAccount + "' and " + " ReceiveAccount =" + " '" + myAccount + "' and "+ "MessageType = "+ "'"+ 0 +"'"+ " )" ,null,null,null,
                "MessageId asc",null);//查询并获得游标
        int count = cursor.getCount();
        sQLiteDatabase.close();
        return count;
    }

    //有聊天消息的账号
    synchronized public String [] queryMessageAccount(String account){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query(true,"MessageTable", new String[]{"ReceiveAccount,SendAccount"},
                "( ReceiveAccount = " + "'"+account + "'and MessageType = "+"' "+ 0 +"' )"+ "or "+ "( SendAccount = '" + account + "' and MessageType = "+"' "+ 1 +"' )",null,null,null,"MessageID desc",null);//查询并获得游标
        if(cursor.getCount() == 0){
            sQLiteDatabase.close();
            return null;
        }

        String [] resultReceiveAccount = new String[cursor.getCount()];
        String [] resultSendAccount = new String[cursor.getCount()];
        int lenSend = 0;
        int lenRec = 0;
        while(cursor.moveToNext()){
            if(cursor.getString(0)!=null)
            resultReceiveAccount[lenRec++] = new String(cursor.getString(0));
            if(cursor.getString(1)!=null)
            resultSendAccount[lenSend++] = new String(cursor.getString(1));
        }
        sQLiteDatabase.close();
        List<String> mid = new ArrayList<>();
        for(int i = 0;i<resultReceiveAccount.length;i++) {
            mid.add(resultReceiveAccount[i]);
        }
        for(int i = 0;i<resultSendAccount.length;i++){
            mid.add(resultSendAccount[i]);
        }
        List<String> finalResult = new ArrayList<String>(new HashSet<String>(mid));
        finalResult.remove(account);
        String [] finalResults = (String[]) finalResult.toArray(new String[finalResult.size()]);
        return finalResults;
    }

    //有聊天消息的账号更新
    synchronized public String [] queryMessageUpdateAccount(String account,String[]friendAccount){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query(true,"MessageTable", new String[]{"ReceiveAccount,SendAccount"},
                " ReceiveAccount = " + "'"+account + "' or "+ "SendAccount = '" + account + "'",null,null,null,"MessageID desc",null);//查询并获得游标
        String [] resultReceiveAccount = new String[cursor.getCount()];
        System.out.println(cursor.getCount());
        String [] resultSendAccount = new String[cursor.getCount()];
        int lenSend = 0;
        int lenRec = 0;
        while(cursor.moveToNext()){
            if(cursor.getString(0)!=null)
                resultReceiveAccount[lenRec++] = new String(cursor.getString(0));
            if(cursor.getString(1)!=null)
                resultSendAccount[lenSend++] = new String(cursor.getString(1));
        }
        sQLiteDatabase.close();
        List<String> mid = new ArrayList<>();
        for(int i = 0;i<resultReceiveAccount.length;i++) {
            mid.add(resultReceiveAccount[i]);
        }
        for(int i = 0;i<resultSendAccount.length;i++){
            mid.add(resultSendAccount[i]);
        }
        List<String> finalResult = new ArrayList<String>(new HashSet<String>(mid));

        List<String> delAccount = Arrays.asList(friendAccount);
        HashSet hs1 = new HashSet(finalResult);
        HashSet hs2 = new HashSet(delAccount);
        hs1.removeAll(hs2);
        List<String> s=new ArrayList<>();
        s.addAll(hs1);
        s.remove(account);
        String [] finalResults = (String[]) s.toArray(new String[s.size()]);
        return finalResults;
    }

    //更新接收消息的状态
    synchronized public void updateMessageReceiveType(String myAccount,String friendAccount){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query(false,"MessageTable",new String[]{"MessageId","MessageContent","SendAccount","ReceiveAccount","MessageType","MessageReceiveType"},
                " ( SendAccount = " + "'" + myAccount + "'" + " and "+ "MessageType =" + " '" + 1 + "'"+  " and "+ "ReceiveAccount =" + " '" + friendAccount + "' ) " +
                        "or" + " ( SendAccount =" +" '" + friendAccount + "' and " + " ReceiveAccount =" + " '" + myAccount + "' and "+ "MessageType = "+ "'"+ 0 +"'"+ " )" ,null,null,null,
                "MessageId asc",null);//查询并获得游标
        MessageTable [] messageTables = new MessageTable[cursor.getCount()];
        for (int i=0; cursor.moveToNext() == true; i++ ){
            messageTables[i] = new MessageTable(
                    cursor.getInt(cursor.getColumnIndex("MessageId")),
                    cursor.getString(cursor.getColumnIndex("MessageContent")),
                    cursor.getString(cursor.getColumnIndex("SendAccount")),
                    cursor.getString(cursor.getColumnIndex("ReceiveAccount")),
                    cursor.getInt(cursor.getColumnIndex("MessageType")),
                    cursor.getInt(cursor.getColumnIndex("MessageReceiveType"))
            );
        }
        ContentValues values = new ContentValues();
        values.put("MessageReceiveType",2);
        for(int i =0;i<messageTables.length;i++){
            if(messageTables[i].getMessageReceiveType()!=2)
            sQLiteDatabase.update("MessageTable",values,"MessageId = "+ messageTables[i].getMessageId(),null);
        }
        sQLiteDatabase.close();
    }

    //查询与指定账户最近的消息状态
    synchronized public int queryMessageReceiveType(String myAccount,String friendAccount){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query(false,"MessageTable",new String[]{"MessageId","MessageContent","SendAccount","ReceiveAccount","MessageType","MessageReceiveType"},
                " ( SendAccount = " + "'" + myAccount + "'" + " and "+ "MessageType =" + " '" + 1 + "'"+  " and "+ "ReceiveAccount =" + " '" + friendAccount + "' ) " +
                        "or" + " ( SendAccount =" +" '" + friendAccount + "' and " + " ReceiveAccount =" + " '" + myAccount + "' and "+ "MessageType = "+ "'"+ 0 +"'"+ " )" ,null,null,null,
                null,null);//查询并获得游标
        MessageTable [] messageTables = new MessageTable[cursor.getCount()];
        for (int i=0; cursor.moveToNext() == true; i++ ){
            messageTables[i] = new MessageTable(
                    cursor.getInt(cursor.getColumnIndex("MessageId")),
                    cursor.getString(cursor.getColumnIndex("MessageContent")),
                    cursor.getString(cursor.getColumnIndex("SendAccount")),
                    cursor.getString(cursor.getColumnIndex("ReceiveAccount")),
                    cursor.getInt(cursor.getColumnIndex("MessageType")),
                    cursor.getInt(cursor.getColumnIndex("MessageReceiveType"))
            );
        }
        int len = 0;
        for(int i = len;i<messageTables.length;i++){
            if(messageTables[i]!= null)
                len++;
        }
        len--;
        sQLiteDatabase.close();
        return messageTables[len].getMessageReceiveType();
    }

    //  查询消息的总数量，用于分配messageID
    synchronized public int queryMessageCount(){
//        以读写方式打开
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query("MessageTable",null,null,null,null,null,null);//查询并获得游标
        int count = cursor.getCount();
        sQLiteDatabase.close();
        return  count;
    }


    /*************************************朋友列表的插入*******************************************/
    //插入一个朋友
    synchronized public long insertFriend(FriendListTable friendListTable){
//        以读写方式打开
        SQLiteDatabase sqLiteDatabase = databaseHelp.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.clear();
        contentValues.put("FriendId",friendListTable.getFriendId());
        contentValues.put("MyAccount",friendListTable.getMyAccount());
        contentValues.put("FriendAccount",friendListTable.getFriendAccount());
        contentValues.put("FriendName",friendListTable.getFriendName());
        long result  = sqLiteDatabase.insert("FriendListTable", null, contentValues);
        sqLiteDatabase.close();
        return result;
    }

    synchronized public int queryFriendCount(){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query("FriendListTable",null,null,null,null,null,null);//查询并获得游标
        int count = cursor.getCount();
        sQLiteDatabase.close();
        return count;
    }

    //查询所有朋友
    synchronized public FriendListTable[] queryFriend(){
//        以读写方式打开
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query("FriendListTable",null,null,null,null,null,null);//查询并获得游标
        FriendListTable [] friendListTables = new FriendListTable[cursor.getCount()];
        for (int i=0; cursor.moveToNext() == true; i++ ){
            friendListTables[i] = new FriendListTable(
                    cursor.getString(cursor.getColumnIndex("MyAccount")),
                    cursor.getInt(cursor.getColumnIndex("FriendId")),
                    cursor.getString(cursor.getColumnIndex("FriendAccount")),
                    cursor.getString(cursor.getColumnIndex("FriendName"))
            );
        }
        sQLiteDatabase.close();
        return  friendListTables;
    }
    //查询自己账号的朋友
    synchronized public String[] queryFriendAccount(String account){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query(true,"FriendListTable", new String[]{"FriendAccount"}," MyAccount = " + "'"+account + "'",null,null,null,null,null);//查询并获得游标
        String [] resultFriendAccount = new String[cursor.getCount()];
        int len = 0;
        while (cursor.moveToNext())
            resultFriendAccount[len++] = new String(cursor.getString(0));
        sQLiteDatabase.close();
        return  resultFriendAccount;
    }
    //是否存在这个朋友
    synchronized public boolean isExistFriend(String myAccount,String friendAccount){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query(true,"FriendListTable", new String[]{"FriendAccount"}," MyAccount = " + "'"+myAccount + "' " + "and FriendAccount = '"+ friendAccount + "'",null,null,null,null,null);//查询并获得游标
        int count = cursor.getCount();
        if(count>=1) {
            sQLiteDatabase.close();
            return true;
        }
        else {
            sQLiteDatabase.close();
            return false;
        }

    }

/********************************************个人设置***********************************************/
//  插入一条个人设置

    synchronized public long insertMySettings(MySettingsTable mySettingsTable){
        busyFlag = 1;
        SQLiteDatabase sqLiteDatabase = databaseHelp.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.clear();
        contentValues.put("HeadId",mySettingsTable.getHeadId());
        contentValues.put("Account",mySettingsTable.getAccount());
        contentValues.put("NikeName",mySettingsTable.getNikeName());
        contentValues.put("Age",mySettingsTable.getAge());
        contentValues.put("Address",mySettingsTable.getAddress());
        contentValues.put("PhoneNum",mySettingsTable.getPhoneNum());
        contentValues.put("Sex",mySettingsTable.getSex());
        long result  = sqLiteDatabase.insert("MySettingsTable", null, contentValues);
        sqLiteDatabase.close();
        busyFlag = 0;
        return result;
}
    //更新个人信息
    synchronized public void updateMySettings(MySettingsTable mySettingsTable){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.clear();
        contentValues.put("HeadId",mySettingsTable.getHeadId());
        contentValues.put("NikeName",mySettingsTable.getNikeName());
        contentValues.put("Age",mySettingsTable.getAge());
        contentValues.put("Address",mySettingsTable.getAddress());
        contentValues.put("PhoneNum",mySettingsTable.getPhoneNum());
        contentValues.put("Sex",mySettingsTable.getSex());
        sQLiteDatabase.update("MySettingsTable",contentValues,"Account = '"+ mySettingsTable.getAccount() + "'",null);
        sQLiteDatabase.close();
    }

    synchronized public MySettingsTable queryMySettings(String account){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query(false,"MySettingsTable",new String[]{"HeadId","Account","NikeName","Age","Address","PhoneNum","Sex"},"Account = '" + account +"'" ,null,null,null,
                null,null);//查询并获得游标
        MySettingsTable mySettingsTable= null;
        while(cursor.moveToNext() == true) {
            mySettingsTable = new MySettingsTable(
                    cursor.getInt(cursor.getColumnIndex("HeadId")),
                    cursor.getString(cursor.getColumnIndex("Account")),
                    cursor.getString(cursor.getColumnIndex("NikeName")),
                    cursor.getInt(cursor.getColumnIndex("Age")),
                    cursor.getString(cursor.getColumnIndex("Address")),
                    cursor.getString(cursor.getColumnIndex("PhoneNum")),
                    cursor.getInt(cursor.getColumnIndex("Sex")));
            }
        sQLiteDatabase.close();
        return mySettingsTable;
    }

    //是否存在了此账号信息
    synchronized public boolean queryIsMySettingsAccountExist(String account){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query(true,"MySettingsTable",new String[]{"Account"},"Account = '" + account + "'" ,null,null,null, null,null);//查询并获得游标
        boolean isExist = false;
        if(cursor == null || cursor.getCount() == 0)
            isExist = false;
        else
            isExist =true;

        sQLiteDatabase.close();
        return isExist;
    }

/********************************************头像储存**********************************************/

    //插入头像
    synchronized public long insertHeadView(HeadViewTable headViewTable){
        SQLiteDatabase sqLiteDatabase = databaseHelp.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.clear();
        contentValues.put("HeadView",headViewTable.getHeadView());
        contentValues.put("Account",headViewTable.getAccount());
        long result  = sqLiteDatabase.insert("HeadViewTable", null, contentValues);
        sqLiteDatabase.close();
        return result;
}

    //更新头像
    synchronized public void updateHeadView(HeadViewTable headViewTable){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.clear();
        contentValues.put("HeadView",headViewTable.getHeadView());
        sQLiteDatabase.update("HeadViewTable",contentValues,"Account = '"+ headViewTable.getAccount() + "'",null);
        sQLiteDatabase.close();
    }

    synchronized public HeadViewTable queryHeadView(String account){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query(false,"HeadViewTable",new String[]{"HeadView","Account"},"Account = '" + account +"'" ,null,null,null,
                null,null);//查询并获得游标
        HeadViewTable headViewTable= null;
        if(cursor.moveToNext() == true)
            headViewTable = new HeadViewTable(
                    cursor.getBlob(cursor.getColumnIndex("HeadView")),
                    cursor.getString(cursor.getColumnIndex("Account"))
            );
        sQLiteDatabase.close();
        return headViewTable;
    }

    //是否存在了此账号信息
    synchronized public boolean queryIsHeadViewAccountExist(String account){
        SQLiteDatabase sQLiteDatabase= databaseHelp.getReadableDatabase();
        Cursor cursor = sQLiteDatabase.query(true,"HeadViewTable",new String[]{"Account"},"Account = '" + account + "'" ,null,null,null, null,null);//查询并获得游标
        boolean isExist;
        if(cursor == null || cursor.getCount() == 0)
            isExist = false;
        else
            isExist =true;

        sQLiteDatabase.close();
        return isExist;
    }




}
