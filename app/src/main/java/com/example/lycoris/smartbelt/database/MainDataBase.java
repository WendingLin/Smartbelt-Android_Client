package com.example.lycoris.smartbelt.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lycoris.smartbelt.base.BaseAttributeMethod;

/**
 * Created by Eclair D'Amour on 2016/8/28.
 */
public class MainDataBase extends SQLiteOpenHelper {
    // Table name
    public final static String TABLE_DAYPACE="TABLE_DAYPACE";
    public final static String TABLE_DAYSITTING="TABLE_DAYSITTING";
    public final static String TABLE_ALARM="TABLE_ALARM";
    public final static String TABLE_DAILY="TABLE_DAILY";

    // create the table
    public final static String CREATE_DAYPACE=createDaypace();
    public final static String CREATE_DAYSITTING=createDaysitting();
    public final static String CREATE_ALARM=createAlarm();
    public final static String CREATE_DAILY=createDaily();

    public static String createDaypace() {
        String tempcreateSentence;
        BaseAttributeMethod baseAttributeMethod=new BaseAttributeMethod(TABLE_DAYPACE);
        baseAttributeMethod.addTableData("YEAR","Integer",false,false);
        baseAttributeMethod.addTableData("MONTH","Integer",false,false);
        baseAttributeMethod.addTableData("DAY","Integer",false,false);
        baseAttributeMethod.addTableData("STARTHOUR","Integer",false,false);
        baseAttributeMethod.addTableData("STARTMINUTE","Integer",false,false);
        baseAttributeMethod.addTableData("ENDHOUR","Integer",false,false);
        baseAttributeMethod.addTableData("ENDMINUTE","Integer",false,false);
        baseAttributeMethod.addTableData("PACE","Integer",false,false);
        tempcreateSentence=baseAttributeMethod.createTable();
        tempcreateSentence+=baseAttributeMethod.setupPrimaryKey(7);
        return tempcreateSentence;
    }

    public static String createDaysitting() {
        String tempcreateSentence;
        BaseAttributeMethod baseAttributeMethod=new BaseAttributeMethod(TABLE_DAYSITTING);
        baseAttributeMethod.addTableData("YEAR","Integer",false,false);
        baseAttributeMethod.addTableData("MONTH","Integer",false,false);
        baseAttributeMethod.addTableData("DAY","Integer",false,false);
        baseAttributeMethod.addTableData("STARTHOUR","Integer",false,false);
        baseAttributeMethod.addTableData("STARTMINUTE","Integer",false,false);
        baseAttributeMethod.addTableData("ENDHOUR","Integer",false,false);
        baseAttributeMethod.addTableData("ENDMINUTE","Integer",false,false);
        baseAttributeMethod.addTableData("DURATION","Integer",false,false);
        tempcreateSentence=baseAttributeMethod.createTable();
        tempcreateSentence+=baseAttributeMethod.setupPrimaryKey(7);
        return tempcreateSentence;
    }

    public static String createAlarm() {
        String tempcreateSentence;
        BaseAttributeMethod baseAttributeMethod=new BaseAttributeMethod(TABLE_ALARM);
        baseAttributeMethod.addTableData("ID","Integer",false,false);
        baseAttributeMethod.addTableData("HOUR","Integer",false,0);
        baseAttributeMethod.addTableData("MINUTE","Integer",false,0);
        baseAttributeMethod.addTableData("MONDAY","Integer",false,0);
        baseAttributeMethod.addTableData("TUESDAY","Integer",false,0);
        baseAttributeMethod.addTableData("WEDNESDAY","Integer",false,0);
        baseAttributeMethod.addTableData("THURSDAY","Integer",false,0);
        baseAttributeMethod.addTableData("FRIDAY","Integer",false,0);
        baseAttributeMethod.addTableData("SATURDAY","Integer",false,0);
        baseAttributeMethod.addTableData("SUNDAY","Integer",false,0);
        baseAttributeMethod.addTableData("REPEAT","Integer",false,0);
        baseAttributeMethod.addTableData("OPEN","Integer",false,0);
        baseAttributeMethod.addTableData("AFFAIRS","TEXT",true,false);
        tempcreateSentence=baseAttributeMethod.createTable();
        tempcreateSentence+=baseAttributeMethod.setupPrimaryKey(1);
        return tempcreateSentence;
    }

    public static String createDaily() {
        String tempcreateSentence;
        BaseAttributeMethod baseAttributeMethod=new BaseAttributeMethod(TABLE_DAILY);
        baseAttributeMethod.addTableData("YEAR","Integer",false,false);
        baseAttributeMethod.addTableData("MONTH","Integer",false,false);
        baseAttributeMethod.addTableData("DAY","Integer",false,false);
        baseAttributeMethod.addTableData("NUMBER","Integer",false,false);
        baseAttributeMethod.addTableData("PACE","Integer",false,0);
        baseAttributeMethod.addTableData("DURATION","Integer",false,0);
        tempcreateSentence=baseAttributeMethod.createTable();
        tempcreateSentence+=baseAttributeMethod.setupPrimaryKey(3);
        return tempcreateSentence;
    }


    public MainDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DAYPACE);
        db.execSQL(CREATE_DAYSITTING);
        db.execSQL(CREATE_ALARM);
        db.execSQL(CREATE_DAILY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
    }


}
