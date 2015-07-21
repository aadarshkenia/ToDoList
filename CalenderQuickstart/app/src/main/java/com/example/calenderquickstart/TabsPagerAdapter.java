package com.example.calenderquickstart;

/**
 * Created by aadarsh-ubuntu on 7/17/15.
 */
import com.example.calenderquickstart.AddDetailedEventFragment;
import com.example.calenderquickstart.AddQuickEventFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new AddQuickEventFragment();
            case 1:
                // Games fragment activity
                return new AddDetailedEventFragment();

        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}