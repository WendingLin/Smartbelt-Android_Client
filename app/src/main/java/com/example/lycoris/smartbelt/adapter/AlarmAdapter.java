package com.example.lycoris.smartbelt.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lycoris.smartbelt.R;
import com.example.lycoris.smartbelt.base.BaseAttributeMethod;
import com.example.lycoris.smartbelt.base.OnSwitchCheckedChangeCallBack;
import com.example.lycoris.smartbelt.database.MainDataBase;
import com.example.lycoris.smartbelt.uiwidget.SwitchButton;

import java.util.List;
import java.util.Map;

/**
 * Created by Lycoris on 2016/5/13.
 */
public class AlarmAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, Object>> listItems;
    private LayoutInflater listContainer;

    public final class ListItemView{
        TextView time;
        TextView title;
        ImageView ifRepeat;
        TextView monday;
        TextView tuesday;
        TextView wednesday;
        TextView thursday;
        TextView friday;
        TextView saturday;
        TextView sunday;
        SwitchButton ifOpen;

    }
    private OnSwitchCheckedChangeCallBack onSwitchCheckedChangeCallBack;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    // Alarm adapter constructor
    public AlarmAdapter(Context context, List<Map<String, Object>> listItems) {
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
            convertView = listContainer.inflate(R.layout.listitem_alarm,null);
            //获取控件对象
            listItemView.time=(TextView)convertView.findViewById(R.id.tv_alarm_time);
            listItemView.ifRepeat = (ImageView)convertView.findViewById(R.id.img_alarm_repeat);
            listItemView.title = (TextView)convertView.findViewById(R.id.tv_alarm_title);
            listItemView.sunday= (TextView)convertView.findViewById(R.id.tv_alarm_sunday);
            listItemView.monday= (TextView)convertView.findViewById(R.id.tv_alarm_monday);
            listItemView.tuesday= (TextView)convertView.findViewById(R.id.tv_alarm_tuesday);
            listItemView.wednesday= (TextView)convertView.findViewById(R.id.tv_alarm_wednesdayday);
            listItemView.thursday= (TextView)convertView.findViewById(R.id.tv_alarm_thursday);
            listItemView.friday= (TextView)convertView.findViewById(R.id.tv_alarm_friday);
            listItemView.saturday= (TextView)convertView.findViewById(R.id.tv_alarm_saturday);
            listItemView.ifOpen=(SwitchButton) convertView.findViewById(R.id.swi_alarm_ifopen);
            //Collect the widget to the convertView
            convertView.setTag(listItemView);
        }else {
            listItemView = (ListItemView)convertView.getTag();
        }

        //Setup the attribution of the widget
        listItemView.time.setText((String) listItems.get(position).get("time"));
        returnVisibility((Boolean)listItems.get(position).get("ifRepeat"),listItemView.ifRepeat);
        listItemView.title.setText((String) listItems.get(position).get("title"));
        tvChangeState(listItemView.sunday,(Boolean)listItems.get(position).get("sunday"));
        tvChangeState(listItemView.monday,(Boolean)listItems.get(position).get("monday"));
        tvChangeState(listItemView.tuesday,(Boolean)listItems.get(position).get("tuesday"));
        tvChangeState(listItemView.wednesday,(Boolean)listItems.get(position).get("wednesday"));
        tvChangeState(listItemView.wednesday,(Boolean)listItems.get(position).get("wednesday"));
        tvChangeState(listItemView.thursday,(Boolean)listItems.get(position).get("thursday"));
        tvChangeState(listItemView.friday,(Boolean)listItems.get(position).get("friday"));
        tvChangeState(listItemView.saturday,(Boolean)listItems.get(position).get("saturday"));
        listItemView.ifOpen.setChecked((Boolean)listItems.get(position).get("ifOpen"));
        listItemView.ifOpen.setOnCheckedChangeListener(onCheckedChangeListener);
        onCheckedChangeListener=new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(onSwitchCheckedChangeCallBack !=null){
                    onSwitchCheckedChangeCallBack.onSwitchCheckedChange(position,b);
                    MainDataBase mainDataBase=new MainDataBase(context,"SmartBelt.db",null,1);
                    SQLiteDatabase sqLiteDatabase=mainDataBase.getWritableDatabase();
                    ContentValues contentValues=new ContentValues();
                    contentValues.put("OPEN",new BaseAttributeMethod().fuckSQLiteNoBoolen(b));
                    sqLiteDatabase.update(MainDataBase.TABLE_ALARM,contentValues,"ID=?",new String[]{Integer.toString(selectID)});
                    contentValues.clear();
                }
            }
        };
        return convertView;
    }

    // Control the image attribution
    private void returnVisibility(Boolean visibilityState, ImageView imageView) {
       if (visibilityState)
           imageView.setVisibility(View.VISIBLE);
        else
           imageView.setVisibility(View.GONE);
    }

    // Control the text attribution
    private void tvChangeState(TextView textView,Boolean ifSelected){
        if(ifSelected) {
            textView.getPaint().setFakeBoldText(true);
            textView.setTextColor(Color.parseColor("#50ced6"));
        }else{
            textView.getPaint().setFakeBoldText(false);
            textView.setTextColor(Color.BLACK);
        }
    }

    public void setOnSwitchCheckedChangeCallBack(OnSwitchCheckedChangeCallBack onSwitchCheckedChangeCallBack){
        this.onSwitchCheckedChangeCallBack = onSwitchCheckedChangeCallBack;
    }




}
