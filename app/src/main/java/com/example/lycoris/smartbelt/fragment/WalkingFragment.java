package com.example.lycoris.smartbelt.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lycoris.smartbelt.R;
import com.example.lycoris.smartbelt.activity.DayPaceChartActivity;
import com.example.lycoris.smartbelt.tools.AnimationArcView;
import com.example.lycoris.smartbelt.tools.RiseNumberTextView;
import com.example.lycoris.smartbelt.tools.WeekPaceBarUtils;

/**
 * Created by Eclair D'Amour on 2016/8/26.
 */
public class WalkingFragment extends Fragment implements View.OnClickListener{

    public WalkingFragment() {
    }

    private LinearLayout llPaceArc;
    private LinearLayout llWeekChart;
    private RiseNumberTextView tvRiseNum;
    private TextView tvPercentNum;
    private TextView tvTimeNum;

    private int goal;
    private int pedometer;
    private String currentTime;
    private int[] weekPedometer;
    private String[] weekName;

    private WeekPaceBarUtils weekPaceBarUtils;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle=getArguments();
        goal=bundle.getInt("goal");
        pedometer=bundle.getInt("pedometer");
        currentTime=bundle.getString("currentTime");
        weekPedometer=bundle.getIntArray("weekPedometer");
        weekName=bundle.getStringArray("weekName");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view=layoutInflater.inflate(R.layout.fragment_walking,container,false);
        initUI(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        llPaceArc.removeAllViews();
        llPaceArc.addView(new AnimationArcView(this.getActivity(),pedometer,1,goal));
        animText(tvRiseNum, 3456);
        tvPercentNum.setText("今日完成度");
        tvTimeNum.setText("更新于"+currentTime);
        llPaceArc.setOnClickListener(WalkingFragment.this);

        weekPaceBarUtils=new WeekPaceBarUtils(getContext(),goal,weekPedometer,weekName);
        llWeekChart.addView(weekPaceBarUtils.initBarChartView());

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.ll_pace_arc:
                Intent intent=new Intent(getActivity(), DayPaceChartActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void initUI(View view){
        llPaceArc=(LinearLayout)view.findViewById(R.id.ll_pace_arc);
        llWeekChart=(LinearLayout)view.findViewById(R.id.ll_week_chart);
        tvRiseNum=(RiseNumberTextView) view.findViewById(R.id.tv_rise_num);
        tvPercentNum=(TextView)view.findViewById(R.id.tv_percent_num);
        tvTimeNum=(TextView)view.findViewById(R.id.tv_time_num);
    }

    /**
     * 给一个TextView设置一个数字增长动画
     */
    public static void animText(RiseNumberTextView tv, int number) {
        // 设置数据
        tv.withNumber(number);
        // 设置动画播放时间
        tv.setDuration(1000);
        tv.start();
    }

    Thread aniParaThread=new Thread(){
        @Override
        public void run() {
            try{

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

}
