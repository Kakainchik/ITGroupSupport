package kz.itgroup.itgroupsupport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TokenAdapter extends ArrayAdapter<TokenModel> {

    private LayoutInflater inflater;
    private int layout;
    private List<TokenModel> tokens;

    public TokenAdapter(Context context, int resource, List<TokenModel> tokens) {
        super(context, resource, tokens);

        this.inflater = LayoutInflater.from(context);
        this.layout = resource;
        this.tokens = tokens;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(this.layout, parent, false);

        TextView title_text = (TextView) view.findViewById(R.id.token_text_title);
        TextView description_text = (TextView) view.findViewById(R.id.token_text_description);
        TextView date_text = (TextView) view.findViewById(R.id.token_text_date);
        TextView valid_text = (TextView) view.findViewById(R.id.token_text_valid);

        TokenModel token = tokens.get(position);

        title_text.setText(token.getTitle());
        description_text.setText(token.getDescription());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        date_text.setText(sdf.format(token.getCreateDate().getTime()));
        if (token.isValid()) {
            valid_text.setText(R.string.token_open);
        } else {
            valid_text.setText(R.string.token_closed);
        }

        return view;
    }
}