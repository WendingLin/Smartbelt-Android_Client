package com.example.lycoris.smartbelt.tools;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.SupportBarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.support.SupportColorLevel;
import org.achartengine.renderer.support.SupportSelectedChartType;
import org.achartengine.renderer.support.SupportSeriesRender;
import org.achartengine.renderer.support.SupportXAlign;
import org.achartengine.renderer.support.SupportYAlign;

import java.util.ArrayList;

/**
 * Created by Eclair D'Amour on 2016/9/14.
 */
public class WeekPaceBarUtils extends BaseChartUtils {
    private final static int COLOR_UP_TARGET = Color.parseColor("#ff3a3a");
    private final static int COLOR_LOW_TARGET = Color.parseColor("#aea9a8");

    int[] weekPedometer;
    String[] weekName;
    int goal;


    public WeekPaceBarUtils(Context context, int goal, int[] weekPedometer, String[] weekName) {
        super(context);
        this.goal=goal;
        this.weekName=weekName;
        this.weekPedometer=weekPedometer;
    }

    public View initBarChartView() {
        mXYMultipleSeriesDataSet = new XYMultipleSeriesDataset();

        final SupportSeriesRender barSeriesRender = new SupportSeriesRender();
        //设置柱状图的背景阴影是否可见
        barSeriesRender.setShowBarChartShadow(false);
        //        barSeriesRender.setShowBarChartShadow(Color.DKGRAY);

        mXYRenderer.setTargetValue(8000);
        barSeriesRender.setSelectedChartType(SupportSelectedChartType.BOTH);

        //设置是否使用颜色分级功能
        barSeriesRender.setColorLevelValid(true);
        ArrayList<SupportColorLevel> list = new ArrayList<SupportColorLevel>();

        //如果仅仅以target作为颜色分级，可以使用这个用法
        SupportColorLevel supportColorLevel_a = new SupportColorLevel(0, mXYRenderer.getTargetValue(), COLOR_LOW_TARGET);
        SupportColorLevel supportColorLevel_b = new SupportColorLevel(mXYRenderer.getTargetValue(), mXYRenderer.getTargetValue() * 10, COLOR_UP_TARGET);

        list.add(supportColorLevel_a);
        list.add(supportColorLevel_b);
        barSeriesRender.setColorLevelList(list);


        XYSeries sysSeries = new XYSeries("");
        for (int i = 0; i < weekPedometer.length; i++) {
            sysSeries.add(i, weekPedometer[i]);
            mXYRenderer.addXTextLabel(i, weekName[i]);
        }
        mXYRenderer.addSupportRenderer(barSeriesRender);


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
        mXYRenderer.setBarWidth(35);
        mXYRenderer.setYAxisMax(10000);
        mXYRenderer.setBarSpacing(15);
        mXYRenderer.setXAxisMin(-1);
        mXYRenderer.setXAxisMax(7);
        mXYRenderer.setShowGrid(false);
        //设置XY轴Title的位置，默认是Center
        mXYRenderer.setSupportXAlign(SupportXAlign.LEFT);
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
}
