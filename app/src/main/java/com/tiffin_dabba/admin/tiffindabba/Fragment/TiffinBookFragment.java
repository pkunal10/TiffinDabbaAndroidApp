package com.tiffin_dabba.admin.tiffindabba.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.tiffin_dabba.admin.tiffindabba.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 19-10-2017.
 */

public class TiffinBookFragment extends Fragment {

    TabLayout tbTabLayout;
    ViewPager tbViewPager;
    List<String> tabList;
    String TiffinType="";
    String TiffinId="";

    public TiffinBookFragment() {
    }

    public TiffinBookFragment(String tiffinType, String tiffinId) {
        TiffinType = tiffinType;
        TiffinId = tiffinId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tiffin_book_fragment_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tbTabLayout=(TabLayout) view.findViewById(R.id.TBTablayout);
        tbViewPager=(ViewPager) view.findViewById(R.id.TBViewPager);

        tabList=new ArrayList<>();
        tabList.add(0,"Simple Tiffin");
        tabList.add(1,"Tiffin With Sweet");
        tabList.add(2,"Special Tiffin");
        tabList.add(3,"Monthly Tiffin");

        tbViewPager.setAdapter(new PagerAdapter(getChildFragmentManager(),tabList));
        tbTabLayout.setupWithViewPager(tbViewPager);

        if(TiffinType.equalsIgnoreCase("ST"))
        {
            tbViewPager.setCurrentItem(0);
        }

    }

    private class PagerAdapter extends FragmentPagerAdapter {

        List<String> tabList;
        public PagerAdapter(FragmentManager fm, List<String> tabList) {
            super(fm);
            this.tabList=tabList;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment=new Fragment();

            if(tabList.get(position).equalsIgnoreCase("Simple Tiffin"))
            {
//                if(TiffinId.equalsIgnoreCase(""))
//                {
                    return new SimpleTiffinFragment();
//                }
//                else
//                {
                    //return new SimpleTiffinFragment(TiffinId);
                //}
            }
            else if(tabList.get(position).equalsIgnoreCase("Tiffin With Sweet"))
            {
                return new TiffinWithSweetFragment();
            }
            else if(tabList.get(position).equalsIgnoreCase("Special Tiffin"))
            {
                return new SpecialTiffinFragment();
            }
            else if(tabList.get(position).equalsIgnoreCase("Monthly Tiffin"))
            {
                return new MonthlyBookingFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabList.get(position);
        }
    }
}
