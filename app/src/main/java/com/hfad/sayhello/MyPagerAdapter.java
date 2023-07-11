package com.hfad.sayhello;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hfad.sayhello.Fragments.ChatsFragment;
import com.hfad.sayhello.Fragments.UserFragment;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private int numOfTabs;
    public MyPagerAdapter(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 : return new ChatsFragment();
            case 1 : return new UserFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }


}
