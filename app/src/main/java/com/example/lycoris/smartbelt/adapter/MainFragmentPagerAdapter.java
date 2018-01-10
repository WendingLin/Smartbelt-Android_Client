package com.example.lycoris.smartbelt.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Eclair D'Amour on 2016/8/26.
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter{
    private ArrayList<Fragment> fragmentArrayList;
    public MainFragmentPagerAdapter(FragmentManager fragmentManager, ArrayList<Fragment>fragmentArrayList)
    {
        super(fragmentManager);
        this.fragmentArrayList=fragmentArrayList;
    }
    @Override
    public Fragment getItem(int position){return fragmentArrayList.get(position);}

    @Override
    public int getCount(){return fragmentArrayList.size();}
}
