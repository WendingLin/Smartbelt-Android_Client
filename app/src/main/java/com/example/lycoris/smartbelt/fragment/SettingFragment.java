package com.example.lycoris.smartbelt.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lycoris.smartbelt.R;
import com.example.lycoris.smartbelt.base.BaseTime;
import com.example.lycoris.smartbelt.base.SettingCallBack;
import com.example.lycoris.smartbelt.uiwidget.SwitchButton;

/**
 * Created by Eclair D'Amour on 2016/8/26.
 */
public class SettingFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private RelativeLayout rvSettingGoal;
    private RelativeLayout rvSettingSedtime;
    private RelativeLayout rvSettingSedrepeattime;
    private RelativeLayout rvSettingHeight;
    private RelativeLayout rvSettingWeight;

    private TextView tvSettingGoal;
    private TextView tvSettingSedentary;
    private TextView tvSettingRepeattime;
    private TextView tvSettingHeight;
    private TextView tvSettingWeight;

    private SwitchButton swiSettingRepeatOpen;
    private SwitchButton swiSettingAntistolen;

    private SharedPreferences sharedPreferences;
    private BaseTime baseTime;
    private SettingCallBack callBack;

    int goal;
    int sedtime;
    boolean sedrepeatopen;
    int sedrepeattime;
    boolean antistolen;
    int height;
    int weight;

    public SettingFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context!=null)
            callBack=(SettingCallBack)context;
        sharedPreferences=getContext().getSharedPreferences("data", Activity.MODE_PRIVATE);
        Bundle bundle =getArguments();
        goal=bundle.getInt("goal",goal);
        sedtime=bundle.getInt("sedtime",sedtime);
        sedrepeatopen=bundle.getBoolean("sedrepeatopen",sedrepeatopen);
        sedrepeattime=bundle.getInt("sedrepeattime",sedrepeattime);
        antistolen=bundle.getBoolean("antistolen",antistolen);
        height=bundle.getInt("height",height);
        weight=bundle.getInt("weight",weight);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view= layoutInflater.inflate(R.layout.fragment_setting,container,false);

        rvSettingGoal=(RelativeLayout)view.findViewById(R.id.rv_setting_goal);
        rvSettingSedtime=(RelativeLayout)view.findViewById(R.id.rv_setting_sedtime);
        rvSettingSedrepeattime=(RelativeLayout)view.findViewById(R.id.rv_setting_sedrepeattime);
        rvSettingHeight=(RelativeLayout)view.findViewById(R.id.rv_setting_height);
        rvSettingWeight=(RelativeLayout)view.findViewById(R.id.rv_setting_weight);

        tvSettingGoal=(TextView)view.findViewById(R.id.tv_setting_goal);
        tvSettingSedentary=(TextView)view.findViewById(R.id.tv_setting_sedentary);
        tvSettingRepeattime=(TextView)view.findViewById(R.id.tv_setting_repeattime);
        tvSettingHeight=(TextView)view.findViewById(R.id.tv_setting_height);
        tvSettingWeight=(TextView)view.findViewById(R.id.tv_setting_weight);

        swiSettingAntistolen=(SwitchButton)view.findViewById(R.id.swi_setting_antistolen);
        swiSettingRepeatOpen=(SwitchButton)view.findViewById(R.id.swi_setting_repeatopen);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rvSettingGoal.setOnClickListener(this);
        rvSettingSedtime.setOnClickListener(this);
        rvSettingSedrepeattime.setOnClickListener(this);
        rvSettingHeight.setOnClickListener(this);
        rvSettingWeight.setOnClickListener(this);
        swiSettingAntistolen.setOnCheckedChangeListener(this);
        swiSettingRepeatOpen.setOnCheckedChangeListener(this);

        tvSettingGoal.setText(Integer.toString(goal)+" 步");
        tvSettingSedentary.setText(Integer.toString(sedtime/60)+" h "+Integer.toString(sedtime%60)+" min");
        tvSettingRepeattime.setText(Integer.toString(sedrepeattime)+" min");
        tvSettingHeight.setText(Integer.toString(height)+" cm");
        tvSettingWeight.setText(Integer.toString(weight)+" kg");

        swiSettingAntistolen.setChecked(antistolen);
        swiSettingRepeatOpen.setChecked(sedrepeatopen);


        baseTime=new BaseTime();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rv_setting_goal:
                final EditText etGoal=new EditText(getContext());
                etGoal.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(getContext())
                        .setTitle("请输入您的目标步数")
                        .setView(etGoal)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                goal=Integer.valueOf(etGoal.getText().toString());
                                tvSettingGoal.setText(etGoal.getText().toString()+" 步");
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putInt("goal",goal);
                                editor.commit();
                                callBack.sendGoal(goal);
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();

                break;
            case R.id.rv_setting_sedtime:
                final EditText etSedtime=new EditText(getContext());
                etSedtime.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(getContext())
                        .setTitle("请输入您每日久坐上限时间")
                        .setView(etSedtime)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sedtime=Integer.valueOf(etSedtime.getText().toString());
                                tvSettingSedentary.setText(baseTime.returnFormatHM(sedtime));
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putInt("setSedentary",sedtime);
                                editor.commit();
                                callBack.sendSedtime(sedtime);
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();
                break;
            case R.id.rv_setting_sedrepeattime:
                final EditText etSedrepeattime=new EditText(getContext());
                etSedrepeattime.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(getContext())
                        .setTitle("请输入您久坐提醒时间")
                        .setView(etSedrepeattime)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sedrepeattime=Integer.valueOf(etSedrepeattime.getText().toString());
                                tvSettingRepeattime.setText(Integer.toString(sedtime)+" min");
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putInt("sedrepeattime",sedrepeattime);
                                editor.commit();
                                callBack.sendSedrepeattime(sedrepeattime);
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();
                break;
            case R.id.rv_setting_height:
                final EditText etHeight=new EditText(getContext());
                etHeight.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(getContext())
                        .setTitle("请输入您的身高")
                        .setView(etHeight)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                height=Integer.valueOf(etHeight.getText().toString());
                                tvSettingHeight.setText(etHeight.getText().toString()+" cm");
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putInt("height", height);
                                editor.commit();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();

                break;
            case R.id.rv_setting_weight:
                final EditText etWeight=new EditText(getContext());
                etWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(getContext())
                        .setTitle("请输入您的目标步数")
                        .setView(etWeight)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                weight=Integer.valueOf(etWeight.getText().toString());
                                tvSettingGoal.setText(etWeight.getText().toString()+" kg");
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putInt("weight",weight);
                                editor.commit();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();

                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        switch (compoundButton.getId()){
            case R.id.swi_setting_antistolen:
                editor.putBoolean("openBluetoothAntiLost",b);
                editor.commit();

                break;
            case R.id.swi_setting_repeatopen:
                editor.putBoolean("openRepeatSedentary",b);
                editor.commit();
                callBack.sendSedrepeatopen(b);
                break;
        }

    }
}
