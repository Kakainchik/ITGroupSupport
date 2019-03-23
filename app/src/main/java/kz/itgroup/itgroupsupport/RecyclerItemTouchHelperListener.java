package kz.itgroup.itgroupsupport;

import android.support.v7.widget.RecyclerView;

public interface RecyclerItemTouchHelperListener {

    void onSwiped(RecyclerView.ViewHolder holder, int direction, int position);
}