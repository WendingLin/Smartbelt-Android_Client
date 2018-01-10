package com.example.lycoris.smartbelt.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.lycoris.smartbelt.R;
import com.example.lycoris.smartbelt.adapter.DayPedometerAdapter;
import com.example.lycoris.smartbelt.base.BaseTime;
import com.example.lycoris.smartbelt.database.MainDataBase;
import com.example.lycoris.smartbelt.tools.DayPaceBarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Eclair D'Amour on 2006/9/00.
 */
public class DayPaceChartActivity extends Activity{

    private final static int hourMinute=60;

    MainDataBase mainDataBase;
    SharedPreferences sharedPreferences;
    BaseTime baseTime;
    
    private DayPaceBarUtils dayPaceBarUtils;
    private int[] pedometer={0,255,0,0,0,0,0,0,856,0,0,0,0,564,0,0,0,0,0,0,524,0,0,0};
    private int goal;

    private List<String> time=new ArrayList<String>();
    private List<Integer> pace=new ArrayList<Integer>();
    private List<Map<String, Object>> listItems;

    private LinearLayout llDaypaceChart;
    private ListView listView;
    private DayPedometerAdapter dayPedometerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daypace_chart);

        llDaypaceChart= (LinearLayout) findViewById(R.id.ll_daypace_chart);
        listView=(ListView)findViewById(R.id.lv_daypace);

        mainDataBase=new MainDataBase(DayPaceChartActivity.this,"SmartBelt.db",null,1);
        SQLiteDatabase sqLiteDatabase=mainDataBase.getReadableDatabase();
        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        baseTime=new BaseTime();

        initData(sqLiteDatabase,sharedPreferences,baseTime);

        dayPaceBarUtils = new DayPaceBarUtils(this.getApplicationContext(),pedometer,goal);
        llDaypaceChart.addView(dayPaceBarUtils.initBarChartView());

        setupListView();
    }

    private void initData(SQLiteDatabase sqLiteDatabase, SharedPreferences sharedPreferences, BaseTime baseTime){
        int year=baseTime.getYear();
        int month=baseTime.getMonth();
        int day=baseTime.getDay();

        goal=sharedPreferences.getInt("goal",1000);

        sqLiteDatabase.beginTransaction();
        try {
            Cursor cursor=sqLiteDatabase.rawQuery("Select * From "
                            +MainDataBase.TABLE_DAYPACE
                            +" Where Year=? And Month=? And Day=?"
                    ,new String[]{Integer.toString(year),Integer.toString(month),Integer.toString(day)});
            if(cursor.moveToFirst()){
                do {
                    int startHour=cursor.getInt(cursor.getColumnIndex("STARTHOUR"));
                    int endHour=cursor.getInt(cursor.getColumnIndex("ENDHOUR"));
                    int startMinute=cursor.getInt(cursor.getColumnIndex("STARTMINUTE"));
                    int endMinute=cursor.getInt(cursor.getColumnIndex("ENDMINUTE"));
                    int pace=cursor.getInt(cursor.getColumnIndex("PACE"));
                    cutHours(startHour,endHour,startMinute,endMinute,pace);

                    time.add(baseTime.timeDisplay(startHour,startMinute)
                    +" ~ "+baseTime.timeDisplay(endHour,endMinute));
                    this.pace.add(pace);

                }while(cursor.moveToNext());
            }
            cursor.close();
            sqLiteDatabase.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            sqLiteDatabase.endTransaction();
        }
    }

    private void cutHours(int startHour, int endHour, int startMinute, int endMinute, int pace){
        if(startHour==endHour){
            pedometer[startHour]+=pace;
        }else if(startHour+1==endHour){
            int firstPace=pace*(hourMinute-startMinute)/(hourMinute-startMinute+endMinute);
            int secondPace=pace*endMinute/(hourMinute-startMinute+endMinute);
            pedometer[startHour]+=firstPace;
            pedometer[endHour]+=secondPace;
        }else{
            int firstPace=pace*(hourMinute-startMinute)/(hourMinute-startMinute+endMinute+(endHour-startHour-1)*hourMinute);
            int lastPace=pace*endMinute/(hourMinute-startMinute+endMinute+(endHour-startHour-1)*hourMinute);
            int interPace=pace*hourMinute/(hourMinute-startMinute+endMinute+(endHour-startHour-1)*hourMinute);
            pedometer[startHour]+=firstPace;
            pedometer[endHour]+=lastPace;
            for(int i=startHour+1;i<endHour;i++){
                pedometer[i]+=interPace;
            }
        }
    }

    private List<Map<String,Object>> getListItems(){
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < time.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("time", time.get(i));
            map.put("pace",Integer.toString(pace.get(i)));
            listItems.add(map);
        }
        return listItems;
    }

    private void setupListView(){
        listItems = getListItems();
        dayPedometerAdapter = new DayPedometerAdapter(DayPaceChartActivity.this, listItems); //创建适配
        listView.setAdapter(dayPedometerAdapter);
    }
}
