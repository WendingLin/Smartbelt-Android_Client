package com.example.lycoris.smartbelt.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lycoris.smartbelt.R;
import com.example.lycoris.smartbelt.activity.AlarmSettingActivity;
import com.example.lycoris.smartbelt.adapter.AlarmAdapter;
import com.example.lycoris.smartbelt.base.BaseAttributeMethod;
import com.example.lycoris.smartbelt.base.OnSwitchCheckedChangeCallBack;
import com.example.lycoris.smartbelt.database.MainDataBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eclair D'Amour on 2016/8/26.
 */
public class FunctionFragment extends Fragment implements OnSwitchCheckedChangeCallBack{



    /*---------------------------------------- Value ----------------------------------------*/
    private final static int INTENT_ALARMSETTING=1;

    private ListView listView;
    private AlarmAdapter alarmAdapter;
    private List<Map<String, Object>> listItems;

    List<String> time=new ArrayList<String>();
    List<String> title=new ArrayList<String>();
    List<Boolean> ifRepeat=new ArrayList<Boolean>();
    List<Boolean> sunday=new ArrayList<Boolean>();
    List<Boolean> monday=new ArrayList<Boolean>();
    List<Boolean> tuesday=new ArrayList<Boolean>();
    List<Boolean> wednesday=new ArrayList<Boolean>();
    List<Boolean> thursday=new ArrayList<Boolean>();
    List<Boolean> friday=new ArrayList<Boolean>();
    List<Boolean> saturday=new ArrayList<Boolean>();
    List<Boolean> ifOpen=new ArrayList<Boolean>();


    private BaseAttributeMethod baseAttributeMethod=new BaseAttributeMethod();

    /*---------------------------------------- Life Cycle Of Activity ----------------------------------------*/

    public FunctionFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context!=null){

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,

                             Bundle savedInstanceState){
        View view=layoutInflater.inflate(R.layout.fragment_function,container,false);


        listView = (ListView)view.findViewById(R.id.lv_alarm);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> tempmap=listItems.get(position);
                Intent intent = new Intent(getActivity(),AlarmSettingActivity.class);
                intent.putExtra("ITEM",position);
                FunctionFragment.this.startActivityForResult(intent,INTENT_ALARMSETTING);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    /*---------------------------------------- Initialization & Refreshment ----------------------------------------*/
    // When enter the alarm
    // The application query the data from the database and display it on the screen
    private void initData() {
        clearList();
        MainDataBase mainDataBase=new MainDataBase(getActivity(),"SmartBelt.db",null,1);
        SQLiteDatabase sqLiteDatabase=mainDataBase.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.query(MainDataBase.TABLE_ALARM,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            title.add("Alarm"+Integer.toString(1));
            initDataMethod(cursor);

        }
        for(int i=1;i<3;i++) {
            cursor.moveToNext();
            title.add("Alarm"+Integer.toString(i+1));
            initDataMethod(cursor);
        }
    }


    private void setupListView(){
        initData();
        listItems = getListItems();
        alarmAdapter = new AlarmAdapter(getContext(), listItems); //创建适配
        alarmAdapter.setOnSwitchCheckedChangeCallBack(FunctionFragment.this);
        listView.setAdapter(alarmAdapter);
    }
    private void refreshListView(int id){
        refreshData(id);
        listItems = getListItems();
        alarmAdapter = new AlarmAdapter(getContext(), listItems); //创建适配
        alarmAdapter.setOnSwitchCheckedChangeCallBack(FunctionFragment.this);
        listView.setAdapter(alarmAdapter);
    }
    private void refreshData(int id){
        MainDataBase mainDataBase=new MainDataBase(getActivity(),"SmartBelt.db",null,1);
        SQLiteDatabase sqLiteDatabase=mainDataBase.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.query(MainDataBase.TABLE_ALARM,null,null,null,null,null,null);
        if (cursor.moveToPosition(id)){
            refreshDataMethod(cursor,id);

        }

    }

    // Add the list into the listitem
    private List<Map<String, Object>> getListItems() {
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < 3; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("time", time.get(i));
            map.put("ifRepeat",ifRepeat.get(i));
            map.put("title", title.get(i));
            map.put("sunday", sunday.get(i));
            map.put("monday", monday.get(i));
            map.put("tuesday", tuesday.get(i));
            map.put("wednesday", wednesday.get(i));
            map.put("thursday", thursday.get(i));
            map.put("friday", friday.get(i));
            map.put("saturday", saturday.get(i));
            map.put("ifOpen", ifOpen.get(i));
            listItems.add(map);
        }
        return listItems;
    }

    private void initDataMethod(Cursor cursor){

        time.add(timeDisplay(cursor.getInt(cursor.getColumnIndex("HOUR")),cursor.getInt(cursor.getColumnIndex("MINUTE"))));
        ifRepeat.add(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("REPEAT"))));
        sunday.add(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("SUNDAY"))));
        monday.add(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("MONDAY"))));
        tuesday.add(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("TUESDAY"))));
        wednesday.add(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("WEDNESDAY"))));
        thursday.add(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("THURSDAY"))));
        friday.add(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("FRIDAY"))));
        saturday.add(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("SATURDAY"))));
        ifOpen.add(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("OPEN"))));
    }

    private void refreshDataMethod(Cursor cursor,int id){
        time.set(id,timeDisplay(cursor.getInt(cursor.getColumnIndex("HOUR")),cursor.getInt(cursor.getColumnIndex("MINUTE"))));
        ifRepeat.set(id,baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("REPEAT"))));
        sunday.set(id,baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("SUNDAY"))));
        monday.set(id,baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("MONDAY"))));
        tuesday.set(id,baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("TUESDAY"))));
        wednesday.set(id,baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("WEDNESDAY"))));
        thursday.set(id,baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("THURSDAY"))));
        friday.set(id,baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("FRIDAY"))));
        saturday.set(id,baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("SATURDAY"))));
        ifOpen.set(id,baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("OPEN"))));
    }


    /*---------------------------------------- CallBack ----------------------------------------*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case INTENT_ALARMSETTING:
                if(resultCode== Activity.RESULT_OK){

                    refreshListView(data.getIntExtra("ID",0));

                }

                break;
        }
    }

    @Override
    public void onSwitchCheckedChange(int position,boolean ifchecked){
        /*Synchornize the bluetooth*/

    }

    /*---------------------------------------- Data Process Function ----------------------------------------*/

    private void clearList(){
            time.clear();
            title.clear();
            ifRepeat.clear();
            sunday.clear();
            monday.clear();
            tuesday.clear();
            wednesday.clear();
            thursday.clear();
            friday.clear();
            saturday.clear();
            ifOpen.clear();
        }

    // Change the display type of the time integer(xx:xx)
    private String timeDisplay(Integer hour,Integer minute){
        return timeFormat(hour)+" : "+timeFormat(minute);
    }
    // Change the format of the single integer(0)
    private String timeFormat(Integer integer){
        if(integer>9){
            return Integer.toString(integer);
        }else {
            return "0"+Integer.toString(integer);
        }
    }
}
