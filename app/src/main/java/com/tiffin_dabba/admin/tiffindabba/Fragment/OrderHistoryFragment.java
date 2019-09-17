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
import android.widget.Toast;

import com.tiffin_dabba.admin.tiffindabba.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 14-10-2017.
 */

public class OrderHistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_history_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<String> tabLst = new ArrayList<>();
        TabLayout oHTabLayout;
        ViewPager oHViewPager;

        tabLst.add(0, "Fast Food");
        tabLst.add(1, "Simple Tiffin");
        tabLst.add(2, "Tiffin With Sweet");
        tabLst.add(3, "Special Tiffin");
        tabLst.add(4, "Trial Tiffin");

        oHTabLayout = (TabLayout) view.findViewById(R.id.OHTabLayout);
        oHViewPager = (ViewPager) view.findViewById(R.id.OHViewPager);

        oHViewPager.setAdapter(new PagerAdapter(getChildFragmentManager(), tabLst));
        oHTabLayout.setupWithViewPager(oHViewPager);
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        List<String> tabLst;

        public PagerAdapter(FragmentManager supportFragmentManager, List<String> tabLst) {
            super(supportFragmentManager);
            this.tabLst = tabLst;
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = new Fragment();
            if (tabLst.get(position).equalsIgnoreCase("Fast Food")) {

                return new FastFoodOHFragment();
            }
            else if(tabLst.get(position).equalsIgnoreCase("Simple Tiffin"))
            {
                return new SimpleTiffinOHFragment();
            }
            else if(tabLst.get(position).equalsIgnoreCase("Tiffin With Sweet"))
            {
                return new TiffinWithSweetOHFragment();
            }
            else if(tabLst.get(position).equalsIgnoreCase("Special Tiffin"))
            {
                return new SpecialTiffinOHFragment();
            }
            else if(tabLst.get(position).equalsIgnoreCase("Trial Tiffin"))
            {
                return new TrialTiffinOHFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabLst.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabLst.get(position);
        }
    }
}
