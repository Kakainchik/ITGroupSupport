package kz.itgroup.itgroupsupport;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TokenPagerAdapter extends FragmentPagerAdapter {

    public TokenPagerAdapter(FragmentManager mgr) {
        super(mgr);
    }

    @Override
    public Fragment getItem(int i) {
        return (TokenFragment.newInstance(i));
    }

    @Override
    public int getCount() {
        return (3);
    }
}
