package com.example.lycoris.smartbelt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lycoris.smartbelt.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Eclair D'Amour on 2016/9/18.
 */
public class DayPedometerAdapter extends BaseAdapter{
    private Context context;
    private List<Map<String, Object>> listItems;
    private LayoutInflater listContainer;

    public final class ListItemView{
        TextView tvTime;
        TextView tvPace;
    }

    // Alarm adapter constructor
    public DayPedometerAdapter(Context context, List<Map<String, Object>> listItems) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.listItems = listItems;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return listItems.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    // Get Listview item
    // Respond to the change of the switch

    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final int selectID = position;
        // Define the type of the view
        ListItemView  listItemView = null;
        if (convertView == null) {
            listItemView = new ListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.listitem_daypace,null);
            //获取控件对象
            listItemView.tvTime=(TextView)convertView.findViewById(R.id.tv_daypace_time);
            listItemView.tvPace = (TextView) convertView.findViewById(R.id.tv_daypace_pace);
            //Collect the widget to the convertView
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }

        //Setup the attribution of the widget
        listItemView.tvTime.setText((String) listItems.get(position).get("time"));
        listItemView.tvPace.setText((String) listItems.get(position).get("pace"));

        return convertView;
    }

}
