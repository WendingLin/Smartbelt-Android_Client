package com.example.lycoris.smartbelt.base;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Eclair D'Amour on 2016/8/31.
 */
public class BaseTime {

    Calendar calendar= Calendar.getInstance();

    private int year =calendar.get(Calendar.YEAR)-2000;
    // from zero
    private int month =calendar.get(Calendar.MONTH)+1;
    // from Sunday
    private int week=calendar.get(Calendar.DAY_OF_WEEK);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);
    private int minute = calendar.get(Calendar.MINUTE);
    private int hour = calendar.get(Calendar.HOUR_OF_DAY);
    private int second =calendar.get(Calendar.SECOND);

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getWeek() {
        return week;
    }

    public int getDay() {
        return day;
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }

    public int getSecond() {
        return second;
    }


    public String timeDisplay(Integer hour, Integer minute){
        return timeFormat(hour)+" : "+timeFormat(minute);
    }
    // Change the format of the single integer(0)
   public String timeFormat(Integer integer){
        if(integer>9){
            return Integer.toString(integer);
        }else {
            return "0"+Integer.toString(integer);
        }
    }

    public String currentTime(){
       return timeDisplay(hour,minute);

    }

    public String weekDayFormat(int month,int day){
        return timeFormat(month)+"/"+timeFormat(day);
    }


    public String nowTime(){
        Date dt=new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String nowTime="";
        nowTime= df.format(dt);
        return nowTime;
    }

    public Date getDate(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date=null;
        try {
            date = sdf.parse(nowTime());
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            return date;
        }
    }


    //Get the difference between the date
    public int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    public String getOtherDateString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nextDate = sdf.format(date).toString();
        return nextDate;
    }

    public Date getOtherDate(int difference){
             Date date = new Date();
             long time = (date.getTime() / 1000) + 60 * 60 * 24 * difference;//秒
             date.setTime(time * 1000);//毫秒
             return date;
    }

    public String returnFormatHM(int minute){
        int hour=minute/60;
        int tempminute=minute%60;
        return Integer.toString(hour)+" h "+Integer.toString(tempminute)+" min";
    }
}
