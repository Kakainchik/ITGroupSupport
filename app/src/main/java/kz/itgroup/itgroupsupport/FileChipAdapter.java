package kz.itgroup.itgroupsupport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FileChipAdapter extends RecyclerView.Adapter<FileChipAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<FileChip> chips;
    private OnChipClickListener listener;

    public FileChipAdapter(Context context, List<FileChip> chips, OnChipClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.chips = chips;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.chip_file_list_item, viewGroup, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        FileChip chip = chips.get(i);

        //If is not real\valid file
        if(chip.isShadow()) {
            viewHolder.cancelButton.setVisibility(View.GONE);
            viewHolder.fileImage.setVisibility(View.GONE);
            viewHolder.fileName.setClickable(false);
        } else viewHolder.fileImage.setImageBitmap(chip.getImage());

        viewHolder.fileName.setText(chip.getFileName());
    }

    @Override
    public int getItemCount() {
        return chips.size();
    }

    public interface OnChipClickListener {
        //Click on cancel button for removing
        void onCancelClick(int position);
        //Click on item for opening
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final private ImageView fileImage;
        final private TextView fileName;
        final private ImageButton cancelButton;

        public ViewHolder(@NonNull View itemView, OnChipClickListener listener) {
            super(itemView);
            fileImage = (ImageView)itemView.findViewById(R.id.chip_file_image);
            fileName = (TextView)itemView.findViewById(R.id.chip_file_text);
            cancelButton = (ImageButton)itemView.findViewById(R.id.chip_file_cancel);

            fileName.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));
            cancelButton.setOnClickListener(v -> listener.onCancelClick(getAdapterPosition()));
        }
    }
}
