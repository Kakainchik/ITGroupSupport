package kz.itgroup.itgroupsupport;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TokenFragment extends Fragment {

    private int pageNumber;

    public TokenFragment() {
        // Required empty public constructor
    }

    public static TokenFragment newInstance(int page) {
        TokenFragment fragment = new TokenFragment();
        Bundle args = new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    public static String getTitle(Context context, int position) {
        return "Page " + String.valueOf(position + 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment_token, container, false);

        TextView pageHeader = (TextView) result.findViewById(R.id.displayText);
        String header = String.format("Fragment %d", pageNumber);
        pageHeader.setText(header);

        return result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pageNumber = getArguments() != null ? getArguments().getInt("num") : 0;
    }
}