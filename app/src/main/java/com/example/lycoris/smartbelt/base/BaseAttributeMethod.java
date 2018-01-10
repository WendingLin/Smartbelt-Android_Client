package com.example.lycoris.smartbelt.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lycoris.smartbelt.database.MainDataBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eclair D'Amour on 2016/8/31.
 */
public class BaseAttributeMethod {
    public static final String SUNDAY="SUNDAY";
    public static final String MONDAY="MONDAY";
    public static final String TUESDAY="TUESDAY";
    public static final String WEDNESDAY="WEDNESDAY";
    public static final String THURSDAY="THURSDAY";
    public static final String FRIDAY="FRIDAY";
    public static final String SATURDAY="SATURDAY";

    public BaseAttributeMethod(String tableName){
        this.tableName=tableName;
    }

    public BaseAttributeMethod(){}

    private String tableName;

    private List<String> listAttribute=new ArrayList<String>();

    private List<String> listType=new ArrayList<String>();

    private List<String> listNull=new ArrayList<String>();

    private List<String> listDefault=new ArrayList<>();

    public void addTableData(String attributeName, String typeName, Boolean ifNull, Integer defaultNum){
        listAttribute.add(attributeName);
        listType.add(typeName);
        listNull.add(returnIfNull(ifNull));
        listDefault.add(returnIfDefault(defaultNum));
    }

    public void addTableData(String attributeName, String typeName, Boolean ifNull, Boolean ifDefault){
        listAttribute.add(attributeName);
        listType.add(typeName);
        listNull.add(returnIfNull(ifNull));
        listDefault.add(returnIfDefault(ifDefault));
    }

    private String returnIfNull(Boolean ifNull){
        if(ifNull)
            return "";
        else
            return "NOT NULL";
    }

    private String returnIfDefault(Integer defaultNum){
        return "DEFAULT "+Integer.toString(defaultNum);
    }

    private String returnIfDefault(Boolean ifDefault){
        return "";
    }

    private String createTableSentence(String attribute, String type, String isNull, String isDefault) {
        String tempTableSentence="";
        tempTableSentence+=attribute;
        tempTableSentence+=" ";
        tempTableSentence+=type;
        tempTableSentence+=" ";
        tempTableSentence+=isNull;
        tempTableSentence+=" ";
        tempTableSentence+=isDefault;
        tempTableSentence+=",";
        return tempTableSentence;
    }

    public String createTable(){
        String createSentence="Create table ";
        createSentence+=tableName;
        createSentence+="(";
        for(int i=0;i<listAttribute.size();i++)
            createSentence+=createTableSentence(listAttribute.get(i),listType.get(i),listNull.get(i),listDefault.get(i));
    return createSentence;
    }

    public String setupPrimaryKey(Integer number){
        String tempSetupSentence="";
        switch (number){
            case 1:
                tempSetupSentence= "PRIMARY KEY("
                        +listAttribute.get(0)
                        +"))";
                break;
            case 3:
                tempSetupSentence= "PRIMARY KEY("
                        +listAttribute.get(0)+","
                        +listAttribute.get(1)+","
                        +listAttribute.get(2)
                        +"))";
                break;
            case 7:
                tempSetupSentence= "PRIMARY KEY("
                        +listAttribute.get(0)+","
                        +listAttribute.get(1)+","
                        +listAttribute.get(2)+","
                        +listAttribute.get(3)+","
                        +listAttribute.get(4)+","
                        +listAttribute.get(5)+","
                        +listAttribute.get(6)
                        +"))";
                break;
        }
        return tempSetupSentence;
    }


    public void refreshPedometer(SQLiteDatabase sqLiteDatabase,Integer year, Integer month, Integer day
            ,Integer starthour, Integer startminute, Integer endhour, Integer endminute, Integer pace){
        ContentValues contentValues=new ContentValues();
        contentValues.put("YEAR",year);
        contentValues.put("MONTH",month);
        contentValues.put("DAY",day);
        contentValues.put("STARTHOUR",starthour);
        contentValues.put("STARTMINUTE",startminute);
        contentValues.put("ENDHOUR",endhour);
        contentValues.put("ENDMINUTE",endminute);
        contentValues.put("PACE",pace);
        sqLiteDatabase.beginTransaction();
        try{
            sqLiteDatabase.insert(MainDataBase.TABLE_DAYPACE,null,contentValues);
            contentValues.clear();
                String queryPace="Select * from TABLE_DAILY Where YEAR=? AND MONTH=? AND DAY=?";
                Cursor cursor=sqLiteDatabase.rawQuery(queryPace,new String[]{Integer.toString(year),Integer.toString(month),Integer.toString(day)});
                cursor.moveToFirst();
                    int tempPace=cursor.getInt(cursor.getColumnIndex("PACE"));
                    tempPace+=pace;
            contentValues.put("Pace",tempPace);
            sqLiteDatabase.update(MainDataBase.TABLE_DAILY,contentValues
                    ,"YEAR=? AND MONTH=? AND DAY=?"
                    ,new String[]{Integer.toString(year),Integer.toString(month),Integer.toString(day)});
            contentValues.clear();
            sqLiteDatabase.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public void refreshSedentary(SQLiteDatabase sqLiteDatabase,Integer year, Integer month, Integer day
            ,Integer starthour, Integer startminute, Integer endhour, Integer endminute){
        ContentValues contentValues=new ContentValues();
        contentValues.put("YEAR",year);
        contentValues.put("MONTH",month);
        contentValues.put("DAY",day);
        contentValues.put("STARTHOUR",starthour);
        contentValues.put("STARTMINUTE",startminute);
        contentValues.put("ENDHOUR",endhour);
        contentValues.put("ENDMINUTE",endminute);
        contentValues.put("DURATION",calDuration(starthour,endhour,startminute,endminute));
        sqLiteDatabase.beginTransaction();
        try{
            sqLiteDatabase.insert(MainDataBase.TABLE_DAYSITTING,null,contentValues);
            contentValues.clear();
            String querySitting="Select * from TABLE_DAILY  Where YEAR=? AND MONTH=? AND DAY=?";
            Cursor cursor=sqLiteDatabase.rawQuery(querySitting,new String[]{Integer.toString(year),Integer.toString(month),Integer.toString(day)});
            cursor.moveToFirst();
            int tempDuration=cursor.getInt(cursor.getColumnIndex("DURATION"));
            tempDuration+=calDuration(starthour,endhour,startminute,endminute);
            contentValues.put("DURATION",tempDuration);
            sqLiteDatabase.update(MainDataBase.TABLE_DAILY,contentValues
                    ,"YEAR=? AND MONTH=? AND DAY=?"
                    ,new String[]{Integer.toString(year),Integer.toString(month),Integer.toString(day)});
            contentValues.clear();
            sqLiteDatabase.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            sqLiteDatabase.endTransaction();
        }
    }



    public Boolean fuckSQLiteNoBoolen(Integer integer){
        if(integer==0)
            return false;
        else
            return true;
    }

    public Integer fuckSQLiteNoBoolen(Boolean bool){
        if(bool)
            return 1;
        else
            return 0;
    }

    public int calDuration(int startHour, int endHour, int startMinute, int endMinute){
        if(startHour==endHour)
            return  endMinute-startMinute;
        else
            return (endHour-startHour-1)*60+60-startMinute+endMinute;
    }

    /*public int calPedometer(int startHour,int endHour,int startMinute,int endMinute,int pace){
        int calculatePedometer;

        return calculatePedometer;
    }*/



    private void testCode(SQLiteDatabase sqLiteDatabase){
        /* contentValues.put("HOUR",0);
            contentValues.put("MINUTE",0);
            contentValues.put("MONDAY",0);
            contentValues.put("TUESDAY",0);
            contentValues.put("WEDNESDAY",0);
            contentValues.put("THURSDAY",0);
            contentValues.put("FRIDAY",0);
            contentValues.put("SATURDAY",0);
            contentValues.put("SUNDAY",0);
            contentValues.put("REPEAT",0);
            contentValues.put("OPEN",0);
            contentValues.put("AFFAIRS","NULL");*/
        /*refresh(sqLiteDatabase,16,9,1,17,36,18,25,250);
        Cursor cursor1=sqLiteDatabase.query(NewDataBase.TABLE_DAYPACE,null,null,null,null,null,null);
        if(cursor1.moveToFirst()){
            do {
                Log.d("tasue",Integer.toString(cursor1.getInt(cursor1.getColumnIndex("YEAR"))));
                Log.d("tasue",Integer.toString(cursor1.getInt(cursor1.getColumnIndex("PACE"))));
            }while(cursor1.moveToNext());
        }
        cursor1.close();
        Cursor cursor2=sqLiteDatabase.query(NewDataBase.TABLE_DAILY,null,null,null,null,null,null);
        if(cursor2.moveToFirst()){
            do {
                Log.d("tasueV",Integer.toString(cursor2.getInt(cursor2.getColumnIndex("YEAR"))));
                Log.d("tasueV",Integer.toString(cursor2.getInt(cursor2.getColumnIndex("PACE"))));
            }while(cursor2.moveToNext());
        }
        cursor2.close();*/
    }

}
