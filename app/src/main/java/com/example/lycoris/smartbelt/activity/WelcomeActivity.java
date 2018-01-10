package com.example.lycoris.smartbelt.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;

import com.example.lycoris.smartbelt.R;
import com.example.lycoris.smartbelt.base.BaseAttributeMethod;
import com.example.lycoris.smartbelt.base.BaseTime;
import com.example.lycoris.smartbelt.database.MainDataBase;
import com.example.lycoris.smartbelt.uiwidget.LoadingDialog;

import java.util.Date;

public class WelcomeActivity extends Activity {

    private static final int GOTO_SCAN_ACTIVITY=0;
    private static final int WELCOME_SHORT_TIME=3000;
    private static final int INITIALIZE_COMPLETE=1;

    boolean ifInitialized=false;

    private Dialog dialog;
    private BaseTime baseTime;
    private MainDataBase mainDataBase;
    private SharedPreferences sharedPreferences;
    private BaseAttributeMethod baseAttributeMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        LoadingDialog loadingDialog=new LoadingDialog();
        dialog=loadingDialog.createLoadingDialog(this,"初始化中");
        dialog.show();
        thread.start();
    }

    Thread thread=new Thread() {
        @Override
        public void run() {
            try {
                baseAttributeMethod=new BaseAttributeMethod();
                sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
                if (isFirstRun) {
                    mainDataBase=new MainDataBase(WelcomeActivity.this,"SmartBelt.db",null,1);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    SQLiteDatabase sqLiteDatabase=mainDataBase.getWritableDatabase();
                    baseTime=new BaseTime();
                    initSharedPreference(editor);
                    initDataBase(baseTime,sqLiteDatabase);
                    handler.sendEmptyMessage(INITIALIZE_COMPLETE);
                } else {
                    handler.sendEmptyMessage(INITIALIZE_COMPLETE);
                }
            } catch (Exception e) {
                Toast.makeText(WelcomeActivity.this,"Initiailization Failed",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    };

    private Handler handler=new Handler()
    {
        public void handleMessage(Message message)
        {
            switch (message.what) {
                case GOTO_SCAN_ACTIVITY:
                    Intent intent=new Intent(WelcomeActivity.this,ScanActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case INITIALIZE_COMPLETE:
                    ifInitialized=true;
                    dialog.dismiss();
                    Toast.makeText(WelcomeActivity.this,"Complete initiailization",Toast.LENGTH_SHORT).show();
                    handleInitialization();
                default:
                    break;
            }
        }
    };
    private void handleInitialization(){
        if(ifInitialized)
            handler.sendEmptyMessageDelayed(GOTO_SCAN_ACTIVITY,WELCOME_SHORT_TIME);
    }

    private void initSharedPreference(SharedPreferences.Editor editor){
        editor.putBoolean("isFirstRun", false);
        editor.putString("startDate",baseTime.nowTime());
        editor.putBoolean("openBluetoothAntiLost", false);
        //editor.putBoolean("moduleBluetoothAntiLost", false);
        editor.putInt("goal", 10000);
        editor.putBoolean("openSedentary",true);
        //editor.putInt("sedentaryDuration", 7200);
        editor.putBoolean("openRepeatSedentary",false);
        editor.putInt("setReapeatSedentary",10);
        editor.putInt("setSedentary",360);
        editor.putInt("height",175);
        editor.putInt("weight",65);
        editor.commit();
    }


    // Initialize the database
    // If the database doesn't contain the alarm information
    // Fill the alram table with blank data
    private void initDataBase(BaseTime baseTime, SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.beginTransaction();
        try {
            Cursor cursor=sqLiteDatabase.query(MainDataBase.TABLE_ALARM,null,null,null,null,null,null);

            if(cursor.getCount()==0){
                for(int i=0;i<6;i++)
                {
                    ContentValues contentValues=new ContentValues();
                    contentValues.put("ID",i);
                    sqLiteDatabase.insert(MainDataBase.TABLE_ALARM,null,contentValues);
                    contentValues.clear();
                }
            }
            cursor.close();
            for (int i = -6; i < 1100; i++) {
                Date otherDate = baseTime.getOtherDate(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put("YEAR", otherDate.getYear() - 100);
                contentValues.put("MONTH", otherDate.getMonth() + 1);
                contentValues.put("DAY", otherDate.getDate());
                contentValues.put("NUMBER", i);
                sqLiteDatabase.insert(MainDataBase.TABLE_DAILY, null, contentValues);
                contentValues.clear();
            }
            sqLiteDatabase.setTransactionSuccessful();

            }catch (Exception e){
            e.printStackTrace();
            }finally {
            sqLiteDatabase.endTransaction();
            }
    }

}
