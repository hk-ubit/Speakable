package com.hkubit.thespeakable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * Created by lenovo on 4/23/2018.
 */

class SectionAdapter extends FragmentPagerAdapter {


    public SectionAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:{
                return new FriendsFragment();
            }
            case 1:
            {
                return new ChatsFragment();
            }
            case 2:
                return new RequestFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "Friends";
            case 1:
                return "Chats";
            case 2:
                return "Requests";
                default:
                    return null;
        }
    }
}
