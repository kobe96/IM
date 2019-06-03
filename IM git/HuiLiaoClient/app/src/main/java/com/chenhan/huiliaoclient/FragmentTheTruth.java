package com.chenhan.huiliaoclient;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrinterId;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Base64DataException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentTheTruth extends Fragment {


    private Timer timer;
    public  ArrayList<TheTruth> theTruthsList = new ArrayList<>() ;
    private TheTruthAdapter adapter;
    private ListView listView;
    private Bundle bundle;
    public static TheTruth[] theTruths;
    private int contentCount = 0;
    public static JSONArray jsonArrays = new JSONArray();
    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_the_truth,null);
        //Menu可视
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView = getActivity().findViewById(R.id.Find_The_Truth_ListView);
    }

    @Override
    public void onResume() {
        super.onResume();
        final MainActivity mainActivity = (MainActivity) getActivity();
        final webSocket listener = mainActivity.getListener();
        initView(listener);
        timerRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(timer!=null){
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_send_the_truth,menu);
        final MenuItem item = menu.findItem(R.id.Menu_Send_The_Truth_Item);
//        getActionView()返回你自定义的菜单布局，设置单击事件的目的是，让其单击时执行onOptionsItemSelected，
//          从而只需统一在onOptionsItemSelected处理即可
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(item);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {;
        FragmentSendTheTruth fragmentSendTheTruth = new FragmentSendTheTruth();
        fragmentSendTheTruth.setArguments(bundle);
        getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.frameLayout,fragmentSendTheTruth)
                .commit();
        return super.onOptionsItemSelected(item);
    }


    public Activity Activity(){
        return  getActivity();
    }

    private void initView(webSocket listener) {
        bundle = getArguments();
        listener.sendGetTheTruth();
        adapter = new TheTruthAdapter(getContext(), R.layout.assembly_fragment_find_the_truth_content, theTruthsList);
    }

    public void timerRefresh() {
        timer = new Timer();
        timer.scheduleAtFixedRate( new TimerTask() {
            public void run() {
                AsyncTask asyncTaskInDialog = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        if(jsonArrays.length() > contentCount)
                            return true;
                        else
                            return false;
                    }
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        if((boolean)o == true) {
                            adapter.clear();
                            theTruths = new TheTruth[jsonArrays.length()];
                            for (int i = jsonArrays.length() -1; i >=0 ; i--) {
                                try {
                                    JSONObject object = new JSONObject(jsonArrays.get(i).toString());
                                    if (!object.getString("head_view").equals("null")) {
                                        theTruths[i] = new TheTruth(
                                                UtilHandleImg.getHeadView(Base64.decode(object.get("head_view").toString(), Base64.DEFAULT)),
                                                object.get("send_account").toString(),
                                                object.get("date_time").toString(),
                                                object.get("message").toString(),
                                                Integer.parseInt(object.get("likes").toString()),
                                                Integer.parseInt(object.get("message_id").toString()));
                                        theTruthsList.add(theTruths[i]);
                                    } else {
                                        Resources res = getResources();
                                        Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.headview_man_4);
                                        theTruths[i] = new TheTruth(
                                                bmp,
                                                object.get("send_account").toString(),
                                                object.get("date_time").toString(),
                                                object.get("message").toString(),
                                                Integer.parseInt(object.get("likes").toString()),
                                                Integer.parseInt(object.get("message_id").toString()));
                                        theTruthsList.add(theTruths[i]);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            listView = getActivity().findViewById(R.id.Find_The_Truth_ListView);
                            listView.setAdapter(adapter);
                            contentCount = jsonArrays.length();
                            if(listView != null) {
                                listView.setSelection(0);
                            }
                        }
                    }
                }.execute();
            }
        },0,1000);
    }
}
