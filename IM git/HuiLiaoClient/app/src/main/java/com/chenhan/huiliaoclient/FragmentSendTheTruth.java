package com.chenhan.huiliaoclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.List;

public class FragmentSendTheTruth extends Fragment implements View.OnClickListener {

    private Bundle bundle;
    private DialogSelectFriend dialogSelectFriend;
    private String sendType = "friend";
    private List<String> mAccount;
    private webSocket listener;
    private EditText editText;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_the_truth, null);
        bundle = this.getArguments();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().findViewById(R.id.Send_The_Truth_Send_To_Who_Btn).setOnClickListener(this);
        getActivity().findViewById(R.id.Send_The_Truth_Send).setOnClickListener(this);
        editText = getActivity().findViewById(R.id.Send_The_Truth_EditText);
        final MainActivity mainActivity = (MainActivity) getActivity();
        final webSocket listener = mainActivity.getListener();
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Send_The_Truth_Send_To_Who_Btn:
                dialogSelectFriend = new DialogSelectFriend(getContext(),bundle,getActivity());
                dialogSelectFriend.show();
                dialogSelectFriend.button_send.setOnClickListener(this);
                dialogSelectFriend.button_back.setOnClickListener(this);
                break;
                //主Fragment 发送按钮
            case R.id.Send_The_Truth_Send:
                if(sendType.equals("friend")) {
                    if (!editText.getText().equals("")) {
                        listener.sendTheTruth(editText.getText().toString(), sendType, null);
                    }
                    else{
                        Toast.makeText(getContext(),"不能输入空的内容",Toast.LENGTH_SHORT);
                    }
                }
                else if(sendType.equals("some_friend")){
                    JSONArray jsonArray = new JSONArray();
                    for(int i = 0;i<mAccount.size();i++){
                        jsonArray.put(mAccount.get(i));
                    }
                    if (!editText.getText().equals("")) {
                        listener.sendTheTruth(editText.getText().toString(), sendType, jsonArray.toString());
                    }
                    else{
                        Toast.makeText(getContext(),"不能输入空的内容",Toast.LENGTH_SHORT);
                    }
                }
                Toast.makeText(getContext(),"发送成功",Toast.LENGTH_SHORT);
                getActivity().onBackPressed();
                break;

                //确认联系人按钮
            case R.id.Dialog_Send_To_Btn:
                if(dialogSelectFriend.getCount() == 0) {
                    sendType = "friend";

                }
                else {
                    sendType = "some_friend";
                    mAccount = dialogSelectFriend.getAccount();
                }
                    dialogSelectFriend.dismiss();
                    break;

                //返回按钮
            case R.id.Dialog_Send_To_Back_Btn:
                dialogSelectFriend.dismiss();
                break;
        }



    }
}
