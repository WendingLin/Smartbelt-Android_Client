package com.example.lycoris.smartbelt.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lycoris.smartbelt.R;
import com.example.lycoris.smartbelt.adapter.MainFragmentPagerAdapter;
import com.example.lycoris.smartbelt.base.BaseAttributeMethod;
import com.example.lycoris.smartbelt.base.BaseBLEHandler;
import com.example.lycoris.smartbelt.base.BaseTime;
import com.example.lycoris.smartbelt.base.SettingCallBack;
import com.example.lycoris.smartbelt.database.MainDataBase;
import com.example.lycoris.smartbelt.fragment.FunctionFragment;
import com.example.lycoris.smartbelt.fragment.SettingFragment;
import com.example.lycoris.smartbelt.fragment.SittingFragment;
import com.example.lycoris.smartbelt.fragment.WalkingFragment;
import com.example.lycoris.smartbelt.service.BluetoothLeService;
import com.example.lycoris.smartbelt.uiwidget.LoadingDialog;

import java.util.ArrayList;
import java.util.Date;



/**
 * Created by Eclair D'Amour on 2016/8/26.
 */
public class MainFragmentActivity extends FragmentActivity implements View.OnClickListener
        , SettingCallBack{

    /*---------------------------------------- Value ----------------------------------------*/
    //Add tag
    private final static String TAG = MainFragmentActivity.class.getSimpleName();

    // BLE message transfer standard
    public final static int BLE_MSG_SEND_INTERVAL = 50;
    public final static int BLE_MSG_BUFFER_LEN = 20;

    // Message handler TAG
    private static final int READRSSI=0;
    private static final int INITIALIZE_COMPLETE=1;

    // RSSI value
    private int rssiValue=0;
    private boolean isReadRssi=false;
    private int meanDistanceValue=0;
    private int meanRssiValue=0;
    private int countRssiReturn=0;

    // BLE device control
    private String mDeviceName, mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic characteristicTX, characteristicRX;
    private boolean mConnected = false, characteristicReady = false;
    private String getStringFromBLE="";

    // UI widges
    private TextView tvWalkingNormal,tvWalkingPress,tvSittingNormal,tvSittingPress;
    private TextView tvFunctionNormal,tvFunctionPress,tvSettingNormal,tvSettingPress;
    private TextView tvWalkingTextNormal,tvWalkingTextPress,tvSittingTextNormal,tvSittingTextPress;
    private TextView tvFunctionTextNormal,tvFunctionTextPress,tvSettingTextNormal,tvSettingTextPress;
    private ViewPager viewPager;
    private Dialog dialog;


    // Data construction process
    private MainDataBase mainDataBase;
    private SharedPreferences sharedPreferences;
    private BaseTime baseTime;
    private BaseAttributeMethod baseAttributeMethod;
    private BaseBLEHandler baseBLEHandler;


    private Bundle sedentaryBundle=new Bundle();
    private Bundle functionBundle=new Bundle();
    private Bundle settingBundle=new Bundle();




    // RSSI information message handler
    Handler MyHandler =new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case READRSSI:
                    meanDistanceValue+=calDistance(msg.arg1);
                    countRssiReturn++;
                    break;
                case INITIALIZE_COMPLETE:
                    dialog.dismiss();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    // Code to manage Service lifecycle.
    private final ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBluetoothLeService=((BluetoothLeService.LocalBinder)iBinder).getService();
            if(!mBluetoothLeService.initialize()){
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService=null;
        }
    };


    /*---------------------------------------- Life Cycle Of Activity ----------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        baseTime=new BaseTime();
        baseBLEHandler=new BaseBLEHandler();
        baseAttributeMethod=new BaseAttributeMethod();
        mainDataBase=new MainDataBase(MainFragmentActivity.this,"SmartBelt.db",null,1);
        sharedPreferences=getSharedPreferences("data", MODE_PRIVATE);

        LoadingDialog loadingDialog=new LoadingDialog();
        dialog=loadingDialog.createLoadingDialog(this,"初始化中");
        dialog.show();

        // ☆Open a new thread to refresh data
        initialThread.start();


        // initialize the fragments
        initView();

        // get bluetooth data from the scan activity
        final Intent intent=getIntent();
        mDeviceName=intent.getStringExtra(ScanActivity.EXTRAS_DEVICE_NAME);
        mDeviceAddress=intent.getStringExtra(ScanActivity.EXTRAS_DEVICE_ADDRESS);

        // bind the service with the activity
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);




    }

    // When the activity is resumed, use the overwrite method to regist the receiver
    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);

        }

    }

    // When the activity is paused, use the overwrite method to unregist the receiver
    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    // When the activity is paused, use the overwrite method to unbind the serivice
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        mBluetoothLeService = null;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    /*---------------------------------------- View Method ----------------------------------------*/


    private void initView(){
        tvWalkingNormal=(TextView) findViewById(R.id.tv_walking_normal);
        tvWalkingPress=(TextView) findViewById(R.id.tv_walking_press);
        tvSittingNormal=(TextView) findViewById(R.id.tv_sitting_normal);
        tvSittingPress=(TextView) findViewById(R.id.tv_sitting_press);
        tvFunctionNormal=(TextView) findViewById(R.id.tv_function_normal);
        tvFunctionPress=(TextView) findViewById(R.id.tv_function_press);
        tvSettingNormal=(TextView) findViewById(R.id.tv_setting_normal);
        tvSettingPress=(TextView) findViewById(R.id.tv_setting_press);
        tvWalkingTextNormal=(TextView) findViewById(R.id.tv_walking_text_normal);
        tvWalkingTextPress=(TextView) findViewById(R.id.tv_walking_text_press);
        tvSittingTextNormal=(TextView) findViewById(R.id.tv_sitting_text_normal);
        tvSittingTextPress=(TextView) findViewById(R.id.tv_sitting_text_press);
        tvFunctionTextNormal=(TextView) findViewById(R.id.tv_function_text_normal);
        tvFunctionTextPress=(TextView) findViewById(R.id.tv_function_text_press);
        tvSettingTextNormal=(TextView) findViewById(R.id.tv_setting_text_normal);
        tvSettingTextPress=(TextView) findViewById(R.id.tv_setting_text_press);

        findViewById(R.id.ll_walking).setOnClickListener(this);
        findViewById(R.id.ll_sitting).setOnClickListener(this);
        findViewById(R.id.ll_function).setOnClickListener(this);
        findViewById(R.id.ll_setting).setOnClickListener(this);

        //默认选中第一个
        setTransparency();
        tvWalkingPress.getBackground().setAlpha(255);
        tvWalkingTextPress.setTextColor(Color.argb(255, 69, 192, 26));

        /**ViewPager**/
        viewPager=(ViewPager) findViewById(R.id.main_view_pager);
        WalkingFragment walkingFragment=new WalkingFragment();
        SittingFragment sittingFragment=new SittingFragment();
        FunctionFragment functionFragment=new FunctionFragment();
        SettingFragment settingFragment=new SettingFragment();
        ArrayList<Fragment> fragmentList=new ArrayList<Fragment>();

        walkingFragment.setArguments(setPedometerBundle(mainDataBase.getReadableDatabase(),sharedPreferences));
        sittingFragment.setArguments(setSedentaryBundle(mainDataBase.getReadableDatabase(),sharedPreferences));
        settingFragment.setArguments(setSettingBundle(sharedPreferences));
        
        fragmentList.add(walkingFragment);
        fragmentList.add(sittingFragment);
        fragmentList.add(functionFragment);
        fragmentList.add(settingFragment);
        //ViewPager设置适配器
        viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        //ViewPager显示第一个Fragment
        viewPager.setCurrentItem(0);
        //ViewPager页面切换监听
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //根据ViewPager滑动位置更改透明度
                int diaphaneity_one=(int)(255 * positionOffset);
                int diaphaneity_two=(int)(255 * (1 - positionOffset));
                switch (position){
                    case 0:
                        tvWalkingNormal.getBackground().setAlpha(diaphaneity_one);
                        tvWalkingPress.getBackground().setAlpha(diaphaneity_two);
                        tvSittingNormal.getBackground().setAlpha(diaphaneity_two);
                        tvSittingPress.getBackground().setAlpha(diaphaneity_one);
                        tvWalkingTextNormal.setTextColor(Color.argb(diaphaneity_one, 153, 153, 153));
                        tvWalkingTextPress.setTextColor(Color.argb(diaphaneity_two, 69, 192, 26));
                        tvSittingTextNormal.setTextColor(Color.argb(diaphaneity_two,153,153,153));
                        tvSittingTextPress.setTextColor(Color.argb(diaphaneity_one,69, 192, 26));
                        break;
                    case 1:
                        tvSittingNormal.getBackground().setAlpha(diaphaneity_one);
                        tvSittingPress.getBackground().setAlpha(diaphaneity_two);
                        tvFunctionNormal.getBackground().setAlpha(diaphaneity_two);
                        tvFunctionPress.getBackground().setAlpha(diaphaneity_one);
                        tvSittingTextNormal.setTextColor(Color.argb(diaphaneity_one, 153, 153, 153));
                        tvSittingTextPress.setTextColor(Color.argb(diaphaneity_two, 69, 192, 26));
                        tvFunctionTextNormal.setTextColor(Color.argb(diaphaneity_two,153,153,153));
                        tvFunctionTextPress.setTextColor(Color.argb(diaphaneity_one,69, 192, 26));
                        break;
                    case 2:
                        tvFunctionNormal.getBackground().setAlpha(diaphaneity_one);
                        tvFunctionPress.getBackground().setAlpha(diaphaneity_two);
                        tvSettingNormal.getBackground().setAlpha(diaphaneity_two);
                        tvSettingPress.getBackground().setAlpha(diaphaneity_one);
                        tvFunctionTextNormal.setTextColor(Color.argb(diaphaneity_one, 153, 153, 153));
                        tvFunctionTextPress.setTextColor(Color.argb(diaphaneity_two, 69, 192, 26));
                        tvSettingTextNormal.setTextColor(Color.argb(diaphaneity_two,153,153,153));
                        tvSettingTextPress.setTextColor(Color.argb(diaphaneity_one,69, 192, 26));
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    // Set up transparency of the fragment
    private void setTransparency(){
        tvWalkingNormal.getBackground().setAlpha(255);
        tvSittingNormal.getBackground().setAlpha(255);
        tvFunctionNormal.getBackground().setAlpha(255);
        tvSettingNormal.getBackground().setAlpha(255);
        tvWalkingPress.getBackground().setAlpha(1);
        tvSittingPress.getBackground().setAlpha(1);
        tvFunctionPress.getBackground().setAlpha(1);
        tvSettingPress.getBackground().setAlpha(1);
        tvWalkingTextNormal.setTextColor(Color.argb(255, 153, 153, 153));
        tvSittingTextNormal.setTextColor(Color.argb(255, 153, 153, 153));
        tvFunctionTextNormal.setTextColor(Color.argb(255, 153, 153, 153));
        tvSettingTextNormal.setTextColor(Color.argb(255, 153, 153, 153));
        tvWalkingTextPress.setTextColor(Color.argb(0, 69, 192, 26));
        tvSittingTextPress.setTextColor(Color.argb(0, 69, 192, 26));
        tvFunctionTextPress.setTextColor(Color.argb(0, 69, 192, 26));
        tvSettingTextPress.setTextColor(Color.argb(0, 69, 192, 26));
    }

    // implement the interface of the onclicklisten
    @Override
    public void onClick(View v) {
        setTransparency();
        tvFunctionNormal.getBackground().setAlpha(255);
        switch (v.getId()){
            case R.id.ll_walking:
                viewPager.setCurrentItem(0, false);
                tvWalkingPress.getBackground().setAlpha(255);
                tvWalkingTextPress.setTextColor(Color.argb(255, 69, 192, 26));
                break;
            case R.id.ll_sitting:
                viewPager.setCurrentItem(1, false);
                tvSittingPress.getBackground().setAlpha(255);
                tvSittingTextPress.setTextColor(Color.argb(255, 69, 192, 26));
                break;
            case R.id.ll_function:
                viewPager.setCurrentItem(2,false);
                tvFunctionNormal.getBackground().setAlpha(0);
                tvFunctionPress.getBackground().setAlpha(255);
                tvFunctionTextPress.setTextColor(Color.argb(255, 69, 192, 26));
                break;
            case R.id.ll_setting:
                viewPager.setCurrentItem(3,false);
                tvSettingPress.getBackground().setAlpha(255);
                tvSettingTextPress.setTextColor(Color.argb(255, 69, 192, 26));
                break;
        }
    }
    

    /*---------------------------------------- Data Process Method ----------------------------------------*/

    // Calculate the distance according to rssi
    private double calDistance(int rssi) {
        double power = (Math.abs(rssi)-68)/(10*3.0);
        return Math.pow(10, power)*100;
    }

    private Bundle setPedometerBundle(SQLiteDatabase sqLiteDatabase,SharedPreferences sharedPreferences){
        Bundle pedometerBundle=new Bundle();
        int goal=sharedPreferences.getInt("goal",1000);
        String currentTime=baseTime.currentTime();
        int[] weekPedometer=getWeekPedometer(sqLiteDatabase,sharedPreferences,baseTime);
        int pedometer=weekPedometer[6];
        String[] weekName=getWeekName(sqLiteDatabase,sharedPreferences,baseTime);

        pedometerBundle.putInt("goal",goal);
        pedometerBundle.putInt("dayPedometer",pedometer);
        pedometerBundle.putString("currentTime",currentTime);
        pedometerBundle.putIntArray("weekPedometer",weekPedometer);
        pedometerBundle.putStringArray("weekName",weekName);
        return pedometerBundle;
    }

    private Bundle setSedentaryBundle(SQLiteDatabase sqLiteDatabase,SharedPreferences sharedPreferences){
        Bundle sedentaryBundle=new Bundle();
        int setSedentary=sharedPreferences.getInt("setSedentary",7200);
        int todaySedentary=0;

        ArrayList<String> time=new ArrayList<String>();
        ArrayList<Integer> duration=new ArrayList<Integer>();

        sqLiteDatabase.beginTransaction();
        try {
            Cursor cursorTotal=sqLiteDatabase.rawQuery("Select Duration From "
                    + MainDataBase.TABLE_DAILY
                    +" Where YEAR=? AND MONTH=? AND DAY=?"
                    ,new String[]{Integer.toString(baseTime.getYear())
                            ,Integer.toString(baseTime.getMonth())
                            ,Integer.toString(baseTime.getDay())});
            if(cursorTotal.moveToFirst())
                todaySedentary=cursorTotal.getInt(cursorTotal.getColumnIndex("DURATION"));
            Cursor cursorDay=sqLiteDatabase.rawQuery("Select * From "
                    + MainDataBase.TABLE_DAYSITTING
                    +" Where YEAR=? AND MONTH=? AND DAY=?"
                    ,new String[]{Integer.toString(baseTime.getYear())
                            ,Integer.toString(baseTime.getMonth())
                            ,Integer.toString(baseTime.getDay())});
            if(cursorDay.moveToFirst())
                do {
                   int startHour=cursorDay.getInt(cursorDay.getColumnIndex("STARTHOUR"));
                   int endHour=cursorDay.getInt(cursorDay.getColumnIndex("ENDHOUR"));
                   int startMinute=cursorDay.getInt(cursorDay.getColumnIndex("STARTMINUTE"));
                   int endMinute=cursorDay.getInt(cursorDay.getColumnIndex("ENDMINUTE"));
                   time.add(baseTime.timeDisplay(startHour,startMinute)+" ~ "+baseTime.timeDisplay(endHour,endMinute));
                   duration.add(cursorDay.getInt(cursorDay.getColumnIndex("DURATION")));
                }while(cursorDay.moveToNext());
            sqLiteDatabase.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            sqLiteDatabase.endTransaction();
        }

        sedentaryBundle.putStringArrayList("time",time);

        sedentaryBundle.putIntegerArrayList("duration",duration);
        sedentaryBundle.putInt("todaySedentary",todaySedentary);
        sedentaryBundle.putInt("setSedentary",setSedentary);

        return sedentaryBundle;
    }

    private Bundle setSettingBundle(SharedPreferences sharedPreferences){
        Bundle bundle=new Bundle();
        int goal=sharedPreferences.getInt("goal",10000);
        int sedtime=sharedPreferences.getInt("setSedentary",360);
        boolean sedrepeatopen=sharedPreferences.getBoolean("openRepeatSedentary",false);
        int sedrepeattime=sharedPreferences.getInt("setReapeatSedentary",10);
        boolean antistolen=sharedPreferences.getBoolean("openBluetoothAntiLost", false);
        int height=sharedPreferences.getInt("height",175);
        int weight=sharedPreferences.getInt("weight",65);
        
        bundle.putInt("goal",goal);
        bundle.putInt("sedtime",sedtime);
        bundle.putBoolean("sedrepeatopen",sedrepeatopen);
        bundle.putInt("sedrepeattime",sedrepeattime);
        bundle.putBoolean("antistolen",antistolen);
        bundle.putInt("height",height);
        bundle.putInt("weight",weight);
        
        return bundle;
    }

    private int[] getWeekPedometer(SQLiteDatabase sqLiteDatabase,SharedPreferences sharedPreferences,BaseTime baseTime){
        int[] weekPedometer={0,0,0,0,0,0,0};
        Date startDate=baseTime.getDate(sharedPreferences.getString("startDate",""));
        sqLiteDatabase.beginTransaction();
        try{
            for(int i=-6;i<1;i++) {
                Date endDate=baseTime.getOtherDate(i);
                Cursor cursor=sqLiteDatabase.rawQuery("Select PACE From "
                        + MainDataBase.TABLE_DAILY
                        +" Where NUMBER=?"
                        ,new String[]{Integer.toString(baseTime.getGapCount(startDate,endDate))});
                if (cursor.moveToFirst())
                    weekPedometer[i+6]=cursor.getInt(cursor.getColumnIndex("PACE"));
            }
            sqLiteDatabase.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            sqLiteDatabase.endTransaction();
            return weekPedometer;
        }
    }

    private String[] getWeekName(SQLiteDatabase sqLiteDatabase,SharedPreferences sharedPreferences,BaseTime baseTime) {
        String[] weekName={"","","","","","",""};
        int month;
        int day;
        Date startDate=baseTime.getDate(sharedPreferences.getString("startDate",""));
        sqLiteDatabase.beginTransaction();
        try{
            for(int i=-6;i<1;i++) {
                Date endDate=baseTime.getOtherDate(i);
                Cursor cursor=sqLiteDatabase.rawQuery("Select * From "
                                + MainDataBase.TABLE_DAILY
                                +" Where NUMBER=?"
                        ,new String[]{Integer.toString(baseTime.getGapCount(startDate,endDate))});
                if (cursor.moveToFirst()) {
                    month=cursor.getInt(cursor.getColumnIndex("MONTH"));
                    day=cursor.getInt(cursor.getColumnIndex("DAY"));
                    weekName[i+6]=baseTime.weekDayFormat(month,day);
                }

            }
            sqLiteDatabase.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            sqLiteDatabase.endTransaction();
            return weekName;
        }
    }



    /*---------------------------------------- Data Transmission And Bluetooth Method ----------------------------------------*/
    // send the message to the microduino
    private void sendMessage(String msg) {
        int msglen = msg.length();
        Log.d(TAG, "Sending Result=" + msglen + ":" + msg);

        if (characteristicReady && (mBluetoothLeService != null)
                && (characteristicTX != null) && (characteristicRX != null)) {

            for (int offset = 0; offset < msglen; offset += BLE_MSG_BUFFER_LEN) {
                characteristicTX.setValue(msg.substring(offset, Math.min(offset + BLE_MSG_BUFFER_LEN, msglen)));
                mBluetoothLeService.writeCharacteristic(characteristicTX);
                wait_ble(BLE_MSG_SEND_INTERVAL);
            }
        } else {
            Toast.makeText(MainFragmentActivity.this, getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
        }
    }

    // Refresh the microduino RTC module
    private void sendRTC() {
        int[] refresh=new int[8];
        boolean ifSuccess=false;
        refresh[0]=0;
        refresh[1]=baseTime.getWeek();
        refresh[2]=baseTime.getYear();
        refresh[3]=baseTime.getMonth();
        refresh[4]=baseTime.getDay();
        refresh[5]=baseTime.getHour();
        refresh[6]=baseTime.getMinute();
        refresh[7]=baseTime.getSecond();
        //sendMessage(baseBLEHandler.cutEncode(refresh));
        //ifSuccess= baseAttributeMethod.fuckSQLiteNoBoolen(baseBLEHandler.cutDecode(getStringFromBLE)[1]);
        return;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // pause the thread to create the interval of sending message
    public void wait_ble(int i) {
        try {
            Thread.sleep(i);
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public void sendGoal(int goal) {

    }

    @Override
    public void sendSedrepeatopen(boolean sedrepeatopen) {

    }

    @Override
    public void sendSedrepeattime(int sedrepaettime) {

    }

    @Override
    public void sendSedtime(int sedtime) {

    }


    /*---------------------------------------- Thread ----------------------------------------*/
    Thread initialThread=new Thread(){
        @Override
        public void run(){
            try{
                SQLiteDatabase sqLiteDatabase=mainDataBase.getWritableDatabase();

                setPedometerBundle(sqLiteDatabase,sharedPreferences);
                setSedentaryBundle(sqLiteDatabase,sharedPreferences);

                Message message=new Message();
                message.what=INITIALIZE_COMPLETE;
                MyHandler.sendMessage(message);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    // RSSI information management thread
    Thread readRssiThread =new Thread(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            int count=0;
            super.run();
            while (isReadRssi){
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                count++;
                if(mBluetoothLeService!=null)
                {
                    if(mBluetoothLeService.readRemoteRssi()){

                        rssiValue=mBluetoothLeService.getrssi();
                    }
                }
                Message message=new Message();
                message.what=READRSSI;
                message.arg1=rssiValue;
                message.arg2=count;
                MyHandler.sendMessage(message);
                if(count==6)
                    count=0;
            }
        }

    };

    /*---------------------------------------- Broadcast Receiver ----------------------------------------*/
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // Bluetooth connected
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;

                //invalidateOptionsMenu();
                //displayData(Integer.toString(rssivalue));
            }

            // Bluetooth disconnnected
            // ☆Enable the reconnected alertdialog
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;

                new AlertDialog.Builder(MainFragmentActivity.this)
                        .setTitle("Error")
                        .setMessage("Fail to connect the BLE device" +
                                "\nPlease connect again.")
                        .setPositiveButton("Reconnect", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
                //invalidateOptionsMenu();
            }

            // Core serial connection management
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BluetoothGattService gattService = mBluetoothLeService.getSoftSerialService();
                // Enable the reconnected alertdialog
                if (gattService == null) {
                    Toast.makeText(MainFragmentActivity.this, getString(R.string.without_service), Toast.LENGTH_SHORT).show();
                    wait_ble(2000);
                    return;
                }

                if(mDeviceName.startsWith("Microduino")) {
                    characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_MD_RX_TX);
                }else if(mDeviceName.startsWith("EtOH")) {
                    characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_ETOH_RX_TX);
                }
                characteristicRX = characteristicTX;

                // Core serial connected successfully
                // ☆refresh RTC module in microdule
                // ☆Enable the anti-lost system if the function is opened (receive RSSI)
                // ☆Start the thread to synchronize the data to the microduino
                if (characteristicTX != null) {
                    mBluetoothLeService.setCharacteristicNotification(characteristicTX, true);
                    characteristicReady = true;
                    sendRTC();
                    isReadRssi=true;
                    readRssiThread.start();
                }

                // Core serial connected unsuccessfully
                // ☆Enable the reconnected alertdialog
                else {

                    new AlertDialog.Builder(MainFragmentActivity.this)
                            .setTitle("Error")
                            .setMessage("Fail to connect the BLE device" +
                                    "\nPlease connect again.")
                            .setPositiveButton("Reconnect", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                 getStringFromBLE=intent.getStringExtra(mBluetoothLeService.EXTRA_DATA);
            }
        }
    };



}
