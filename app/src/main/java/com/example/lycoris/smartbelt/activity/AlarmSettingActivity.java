package com.example.lycoris.smartbelt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.lycoris.smartbelt.R;
import com.example.lycoris.smartbelt.base.BaseAttributeMethod;
import com.example.lycoris.smartbelt.database.MainDataBase;
import com.example.lycoris.smartbelt.uiwidget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eclair D'Amour on 2016/9/2.
 */
public class AlarmSettingActivity extends Activity implements View.OnClickListener{

    /*---------------------------------------- Value ----------------------------------------*/
    private TimePicker timePicker;
    private SwitchButton swi_alarmsetting_ifrepeat;
    private Button btn_alarmsetting_ok;
    private Button btn_alarmsetting_set;
    private Integer returnItem;
    final String [] weekdayItems={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

    private BaseAttributeMethod baseAttributeMethod=new BaseAttributeMethod();

    List<Integer> multiChoiceID=new ArrayList<Integer>();
    List<Integer> tempMultiChoiceID=new ArrayList<Integer>();

    private MainDataBase mainDataBase=new MainDataBase(AlarmSettingActivity.this,"SmartBelt.db",null,1);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmsetting);
        Intent getIntent=getIntent();
        returnItem=getIntent.getIntExtra("ITEM",0);
        setupUI();
        initInfo(mainDataBase.getReadableDatabase());
        btn_alarmsetting_ok.setOnClickListener(this);
        btn_alarmsetting_set.setOnClickListener(this);
    }




    private void setupUI(){
        timePicker=(TimePicker)findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        swi_alarmsetting_ifrepeat=(SwitchButton) findViewById(R.id.swi_alarmsetting_ifrepeat);
        btn_alarmsetting_ok=(Button)findViewById(R.id.btn_alarmsetting_ok);
        btn_alarmsetting_set=(Button)findViewById(R.id.btn_alarmsetting_set);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_alarmsetting_set:

                AlertDialog.Builder builder=new AlertDialog.Builder(AlarmSettingActivity.this);
                tempMultiChoiceID.addAll(multiChoiceID);
                builder.setTitle("Choose the date");
                builder.setMultiChoiceItems(weekdayItems, displayInfo(multiChoiceID), new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked)
                            tempMultiChoiceID.add(which);
                        else
                            tempMultiChoiceID.remove(tempMultiChoiceID.indexOf(which));
                    }
                });
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        multiChoiceID.clear();
                        multiChoiceID.addAll(tempMultiChoiceID);
                        tempMultiChoiceID.clear();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempMultiChoiceID.clear();
                    }
                });
                builder.create().show();
                break;
            case R.id.btn_alarmsetting_ok:
                refreshDataBase(mainDataBase.getWritableDatabase());
                Intent intent=new Intent();
                intent.putExtra("ID",returnItem);
                setResult(RESULT_OK,intent);
                finish();
                break;
            default:
                break;
        }
    }

    private boolean[] displayInfo(List<Integer>list){
        boolean[] weekdayinfo=new boolean[]{false, false, false, false, false, false, false};
        if (!list.isEmpty()){
            for(int i=0;i<list.size();i++)
                weekdayinfo[list.get(i)]=true;
        }
        return weekdayinfo;
    }

    private void initInfo(SQLiteDatabase sqLiteDatabase){
        Log.d("hellofuck","hellofuck1");
        sqLiteDatabase.beginTransaction();
        try{
            Log.d("hellofuck","hellofuck2");
            Cursor cursor=sqLiteDatabase.rawQuery("Select * from "+MainDataBase.TABLE_ALARM+" where ID=?",new String[]{Integer.toString(returnItem)});
            if(cursor.moveToFirst()){
                Log.d("hellofuck","hellofuck3");
                timePicker.setCurrentHour((cursor.getInt(cursor.getColumnIndex("HOUR"))));
                timePicker.setCurrentMinute((cursor.getInt(cursor.getColumnIndex("MINUTE"))));
                if(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex(baseAttributeMethod.SUNDAY))))
                    multiChoiceID.add(0);
                if(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex(baseAttributeMethod.MONDAY))))
                    multiChoiceID.add(1);
                if(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex(baseAttributeMethod.TUESDAY))))
                    multiChoiceID.add(2);
                if(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex(baseAttributeMethod.WEDNESDAY))))
                    multiChoiceID.add(3);
                if(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex(baseAttributeMethod.THURSDAY))))
                    multiChoiceID.add(4);
                if(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex(baseAttributeMethod.FRIDAY))))
                    multiChoiceID.add(5);
                if(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex(baseAttributeMethod.SATURDAY))))
                    multiChoiceID.add(6);
                if(baseAttributeMethod.fuckSQLiteNoBoolen(cursor.getInt(cursor.getColumnIndex("REPEAT"))))
                   swi_alarmsetting_ifrepeat.setChecked(true);
            }
            sqLiteDatabase.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {

            sqLiteDatabase.endTransaction();
        }
    }

    private void refreshDataBase(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.beginTransaction();
        try{
            ContentValues contentValues=new ContentValues();
            contentValues.put("HOUR",timePicker.getCurrentHour());
            contentValues.put("MINUTE",timePicker.getCurrentMinute());
            contentValues.put(baseAttributeMethod.SUNDAY,baseAttributeMethod.fuckSQLiteNoBoolen(displayInfo(multiChoiceID)[0]));
            contentValues.put(baseAttributeMethod.MONDAY,baseAttributeMethod.fuckSQLiteNoBoolen(displayInfo(multiChoiceID)[1]));
            contentValues.put(baseAttributeMethod.TUESDAY,baseAttributeMethod.fuckSQLiteNoBoolen(displayInfo(multiChoiceID)[2]));
            contentValues.put(baseAttributeMethod.WEDNESDAY,baseAttributeMethod.fuckSQLiteNoBoolen(displayInfo(multiChoiceID)[3]));
            contentValues.put(baseAttributeMethod.THURSDAY,baseAttributeMethod.fuckSQLiteNoBoolen(displayInfo(multiChoiceID)[4]));
            contentValues.put(baseAttributeMethod.FRIDAY,baseAttributeMethod.fuckSQLiteNoBoolen(displayInfo(multiChoiceID)[5]));
            contentValues.put(baseAttributeMethod.SATURDAY,baseAttributeMethod.fuckSQLiteNoBoolen(displayInfo(multiChoiceID)[6]));
            contentValues.put("REPEAT",baseAttributeMethod.fuckSQLiteNoBoolen(swi_alarmsetting_ifrepeat.isChecked()));
            contentValues.put("OPEN",baseAttributeMethod.fuckSQLiteNoBoolen(true));
            sqLiteDatabase.update(MainDataBase.TABLE_ALARM,contentValues,"ID=?",new String[]{Integer.toString(returnItem)});
            contentValues.clear();
            sqLiteDatabase.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            sqLiteDatabase.endTransaction();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(AlarmSettingActivity.this);
        builder.setTitle("Attention!");
        builder.setMessage("Would you like to update the settings?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                refreshDataBase(mainDataBase.getWritableDatabase());
                Intent intent=new Intent();
                intent.putExtra("ID",returnItem);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        builder.setNegativeButton("Abandon", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });
        builder.show();
    }
}
