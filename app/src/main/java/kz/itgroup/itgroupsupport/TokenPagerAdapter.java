package kz.itgroup.itgroupsupport;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TokenPagerAdapter extends FragmentPagerAdapter {

    private Context context = null;

    public TokenPagerAdapter(Context context, FragmentManager mgr) {
        super(mgr);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        return (TokenFragment.newInstance(i));
    }

    @Override
    public int getCount() {
        return (3);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return (TokenFragment.getTitle(context, position));
    }
}
