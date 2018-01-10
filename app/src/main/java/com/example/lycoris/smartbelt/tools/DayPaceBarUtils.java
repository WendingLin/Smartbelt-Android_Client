package com.example.lycoris.smartbelt.tools;

import android.content.Context;
import android.view.View;

import com.example.lycoris.smartbelt.base.BaseTime;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.SupportBarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.support.SupportSelectedChartType;
import org.achartengine.renderer.support.SupportSeriesRender;
import org.achartengine.renderer.support.SupportXAlign;
import org.achartengine.renderer.support.SupportYAlign;

/**
 * Created by wangjia on 30/06/14.
 */
public class DayPaceBarUtils extends BaseChartUtils {

    BaseTime baseTime;

    int[] dayPedometer;
    //int[] weekPedometer;
    String[] hourName;

    int goal;


    public DayPaceBarUtils(Context context,int[] dayPedometer,int goal) {
        super(context);
        this.dayPedometer=dayPedometer;
        this.goal=goal;
    }

    public View initBarChartView() {
        mXYMultipleSeriesDataSet = new XYMultipleSeriesDataset();

        final SupportSeriesRender barSeriesRender = new SupportSeriesRender();
        //设置柱状图的背景阴影是否可见
        barSeriesRender.setShowBarChartShadow(false);

        barSeriesRender.setSelectedChartType(SupportSelectedChartType.BOTH);

        //设置是否使用颜色分级功能
        barSeriesRender.setColorLevelValid(false);

        hourName=getHourName();

        XYSeries sysSeries = new XYSeries("");
        for (int i = 0; i < dayPedometer.length; i++) {
            sysSeries.add(i, dayPedometer[i]);
            mXYRenderer.addXTextLabel(i, hourName[i]);
        }
        mXYRenderer.addSupportRenderer(barSeriesRender);

        mXYRenderer.setTargetLineVisible(false);

        mXYMultipleSeriesDataSet.addSeries(sysSeries);
        View chartView = ChartFactory.getSupportBarChartView(mContext, mXYMultipleSeriesDataSet,
                mXYRenderer, SupportBarChart.Type.STACKED);
        chartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicalView graphicalView = (GraphicalView) v;
                graphicalView.handPointClickEvent(barSeriesRender,"SupportBar");
            }
        });
        return chartView;
    }

    @Override
    protected void setRespectiveRender(XYMultipleSeriesRenderer render) {
        mXYRenderer.setBarWidth(20);
        mXYRenderer.setBarSpacing(10);
        mXYRenderer.setXAxisMin(-1);
        mXYRenderer.setXAxisMax(24);
        mXYRenderer.setYAxisMin(0);
        mXYRenderer.setYAxisMax(2000);
        mXYRenderer.setShowGrid(false);
        mXYRenderer.setChartTitle("当日步数统计");
        mXYRenderer.setXTitle("每小时步数统计");
        mXYRenderer.setYTitle("");
        //设置XY轴Title的位置，默认是Center
        mXYRenderer.setSupportXAlign(SupportXAlign.CENTER);
        mXYRenderer.setSupportYAlign(SupportYAlign.TOP);
    }

    @Override
    protected XYSeriesRenderer getSimpleSeriesRender(int color) {
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setColor(color);
        renderer.setDisplayChartValues(true);  // 设置是否在点上显示数据
        renderer.setPointStrokeWidth(4f);
        renderer.setChartValuesTextSize(14f);


        //        renderer.setGradientStart(0,Color.GRAY);  //可以设置柱状图颜色的渐变
        //        renderer.setGradientStop(10,Color.GREEN);
        //        renderer.setGradientEnabled(false);
        return renderer;
    }

    private String[] getHourName(){
        baseTime=new BaseTime();
        String[] tempWeek=new String[24];
        for (int i=0;i<24;i++)
            if(i==0||i==12||i==23)
            tempWeek[i]=Integer.toString(i);
        else tempWeek[i]="";
        return tempWeek;
    }

}
