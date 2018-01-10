package com.example.lycoris.smartbelt.base;

/**
 * Created by Eclair D'Amour on 2016/9/7.
 */
public interface SettingCallBack {

    public void sendGoal(int goal);

    public void sendSedtime(int sedtime);

    public void sendSedrepeatopen(boolean sedrepeatopen);

    public void sendSedrepeattime(int sedrepaettime);

}
