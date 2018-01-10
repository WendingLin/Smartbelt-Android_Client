package com.example.lycoris.smartbelt.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lycoris.smartbelt.R;
import com.example.lycoris.smartbelt.adapter.DaySedentaryAdapter;
import com.example.lycoris.smartbelt.tools.SittingPercentageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eclair D'Amour on 2016/8/26.
 */
public class SittingFragment extends Fragment{

    private SittingPercentageView sittingPercentageView;
    private ListView listView;
    private TextView tvTodaySedentary;

    int setSedentary=7200;
    int todaySedentary=2514;


    private ArrayList<Integer> duration=new ArrayList<Integer>();
    private ArrayList<String> time=new ArrayList<String>();
    private List<Map<String, Object>> listItems;

    private int mTotalProgress;
    private int mCurrentProgress;

    private DaySedentaryAdapter daySedentaryAdapter;

    public SittingFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle=getArguments();
        time=bundle.getStringArrayList("time");
        duration=bundle.getIntegerArrayList("duration");
        todaySedentary=bundle.getInt("todaySedentary");
        setSedentary=bundle.getInt("setSedentary");
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view=layoutInflater.inflate(R.layout.fragment_sitting,container,false);

        sittingPercentageView = (SittingPercentageView)view.findViewById(R.id.sitting_percentage);
        listView=(ListView)view.findViewById(R.id.ll_sedentary);
        tvTodaySedentary=(TextView)view.findViewById(R.id.tv_today_sedentary);
        return view;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initVariable();
        setupListView();
        new Thread(new ProgressRunable()).start();
    }

    private void initVariable() {

        mCurrentProgress =100;
        mTotalProgress= 100-100*60/setSedentary;
        if(mTotalProgress<0){
            mTotalProgress=0;
        }
        if(mTotalProgress==100)
            tvTodaySedentary.setText("No Sedentary!\nIt sounds like that you take a standing life");
        else
            tvTodaySedentary.setText("You keep sedentary for about\n"+Integer.toString(1)+" h "+Integer.toString(todaySedentary%60)+" min");
    }


    private List<Map<String,Object>> getListItems(){
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < time.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("time", time.get(i));
            map.put("duration",Integer.toString(duration.get(i)));
            listItems.add(map);
        }
        return listItems;
    }

    private void setupListView(){
        listItems = getListItems();
        daySedentaryAdapter = new DaySedentaryAdapter(getContext(), listItems); //创建适配
        listView.setAdapter(daySedentaryAdapter);
    }

    class ProgressRunable implements Runnable {

        @Override
        public void run() {
            Log.d("fick","fuck");
            while (mCurrentProgress > mTotalProgress) {
                mCurrentProgress -= 2;
                sittingPercentageView.setProgress(mCurrentProgress);
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
