package kz.itgroup.itgroupsupport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TokenAdapter extends RecyclerView.Adapter<TokenAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private int layout;
    private List<TokenFile> tokens;

    public TokenAdapter(Context context, int resource, List<TokenFile> tokens) {

        this.inflater = LayoutInflater.from(context);
        this.layout = resource;
        this.tokens = tokens;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.token_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TokenAdapter.ViewHolder viewHolder, int i) {

        //Getting token by id
        TokenModel token = tokens.get(i).getToken();
        //Setting title
        viewHolder.title_text.setText(token.getTitle());
        //Setting body text
        viewHolder.description_text.setText(token.getDescription());

        //Set state text
        switch(token.getState()){
            case SENT: {
                viewHolder.valid_text.setText(R.string.title_token_closed);
                viewHolder.valid_text.setTextColor(ContextCompat.getColor(
                        inflater.getContext(),
                        R.color.colorSentTokenText));
                break;
            }
            case HANDLED: {
                viewHolder.valid_text.setText(R.string.title_token_handled);
                viewHolder.valid_text.setTextColor(ContextCompat.getColor(
                        inflater.getContext(),
                        R.color.colorHandledTokenText));
                break;
            }
            case IN_NOTEBOOK: {
                viewHolder.valid_text.setText(R.string.title_token_open);
                viewHolder.valid_text.setTextColor(ContextCompat.getColor(
                        inflater.getContext(),
                        R.color.colorInNoteTokenText));
                break;
            }
        }

        //Получаем дату создания токена
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        viewHolder.date_text.setText(sdf.format(new Date(token.getCreateDate())));
    }

    @Override
    public int getItemCount() {
        return tokens.size();
    }

    public boolean removeItem(int position) {
        try {
            tokens.remove(position);
            notifyItemRemoved(position);
            return true;
        } catch (Exception ex) {
            Log.e("Remove token", ex.getMessage());
            return false;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final RelativeLayout viewBackground;
        public final LinearLayout viewForeground;
        private final TextView title_text, description_text, date_text, valid_text;

        public ViewHolder(@NonNull View view) {
            super(view);

            title_text = (TextView) view.findViewById(R.id.token_text_title);
            description_text = (TextView) view.findViewById(R.id.token_text_description);
            date_text = (TextView) view.findViewById(R.id.token_text_date);
            valid_text = (TextView) view.findViewById(R.id.token_text_valid);
            viewBackground = (RelativeLayout) view.findViewById(R.id.view_item_background);
            viewForeground = (LinearLayout) view.findViewById(R.id.view_item_foreground);
        }
    }
}