package com.chenhan.huiliaoclient;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FragmentFriendList extends Fragment {

    private String[] mAccount;
    private Bitmap [] mBitmaps;
    private String [] mName;
    private ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_friendlist,null);
        bundle = this.getArguments();
        //Menu可视
        setHasOptionsMenu(true);
        ListView listView = v.findViewById(android.R.id.list);
        final webSocket listener = ((MainActivity) getActivity()).getListener();
        initFriendData(listView);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("联系人");
        ListView mListView;
        mListView = getView().findViewById(android.R.id.list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //第position行
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friendAccount = ((TextView)view.findViewById(R.id.Friend_List_View_Account)).getText().toString();
                Bundle doubleContent = new Bundle();
                doubleContent.putString("myAccount",bundle.getString("account"));
                doubleContent.putString("friendAccount",friendAccount);
                FragmentDialog fragmentDialog= new FragmentDialog();
                fragmentDialog.setArguments(doubleContent);
                getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frameLayout, fragmentDialog)
                        .commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mData.clear();
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), mData, R.layout.assembly_fragment_friend_list_listview_content,
                new String[]{"account", "headView","nikeName"}, new int[]{R.id.Friend_List_View_Account, R.id.Friend_List_View_Img,R.id.Friend_List_View_NikeName});
        ListView listView = getView().findViewById(android.R.id.list);
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView iv = (ImageView)view;
                    iv.setImageBitmap((Bitmap) data);
                    return true;
                }else{
                    return false; }
            }

        });
        listView.setAdapter(adapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_friend,menu);
        final MenuItem item = menu.findItem(R.id.Menu_Add_Friend_Item);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
        Intent intent = new Intent(getActivity(), AddFriendActivity.class);
        intent.putExtras(this.getArguments());
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private void initFriendData(ListView listView) {
        mAccount = MainActivity.databaseManagerInMainActivity.queryFriendAccount(bundle.getString("account"));
        mName = new String[mAccount.length];
        mBitmaps = new Bitmap[mAccount.length];
        for (int i = 0; i < mAccount.length; i++) {
            //添加头像控件
            if(MainActivity.databaseManagerInMainActivity.queryMySettings(mAccount[i]).getHeadId() == 1){
                HeadViewTable headViewTable =  MainActivity.databaseManagerInMainActivity.queryHeadView(mAccount[i]);
                mBitmaps[i] = UtilHandleImg.getHeadView(headViewTable.getHeadView());
            }
            else{
                Resources res=getResources();
                Bitmap bmp= BitmapFactory.decodeResource(res, R.mipmap.headview_man_4);
                mBitmaps[i] = bmp;
            }

            MySettingsTable mySettingsTable = MainActivity.databaseManagerInMainActivity.queryMySettings(mAccount[i]);
            mName[i] = mySettingsTable.getNikeName();

            if(!mAccount[i].equals(bundle.getString("account"))) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("account", mAccount[i]);
                item.put("headView", mBitmaps[i]);
                item.put("nikeName",mName[i]);
                mData.add(item);
            }
        }
        /**
         * SimpleAdapter的五个参数：
         * Context:上下文
         * data：数据源（List<Map<String,?>>data）一个有Map组成的集合
         *       每一个Map都会对应ListView列表中的一行
         *       每一个Map(键-值对)中的键必须包含所有在from参数中所指定的键
         *resource: ListView中每一项的布局文件ID
         *from：Map中的键名
         *to：绑定item.xml文件中的view控件ID，与from，Map的键形成对应
         */
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), mData, R.layout.assembly_fragment_friend_list_listview_content,
                new String[]{"account", "headView","nikeName"}, new int[]{R.id.Friend_List_View_Account, R.id.Friend_List_View_Img,R.id.Friend_List_View_NikeName});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView iv = (ImageView)view;
                    iv.setImageBitmap((Bitmap) data);
                    return true;
                }else{
                    return false; }
            }
        });
        listView.setAdapter(adapter);
    }


}


