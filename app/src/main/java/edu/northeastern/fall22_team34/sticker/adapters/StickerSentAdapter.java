package edu.northeastern.fall22_team34.sticker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.sticker.models.Sticker;

public class StickerSentAdapter extends RecyclerView.Adapter<StickerSentAdapter.StickerSentViewHolder> {

    private List<Sticker> stickerList;
    private List<Integer> countList;
    private Context context;

    public StickerSentAdapter(List<Sticker> stickerList, List<Integer> countList, Context context) {
        this.stickerList = stickerList;
        this.countList = countList;
        this.context = context;
    }

    @NonNull
    @Override
    public StickerSentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_container_sticker_sent, parent, false);
        return new StickerSentAdapter.StickerSentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerSentViewHolder holder, int position) {
        if (stickerList != null) {
            Picasso.get().load(stickerList.get(position).imageUri).fit().centerCrop().into(holder.stickerSentImg);
            holder.sendCount.setText("Sent: " + countList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (stickerList != null) {
            return stickerList.size();
        }
        return 0;
    }

    static class StickerSentViewHolder extends RecyclerView.ViewHolder{

        public ImageView stickerSentImg;
        public TextView sendCount;

        public StickerSentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.stickerSentImg = itemView.findViewById(R.id.stickerSentImg);
            this.sendCount = itemView.findViewById(R.id.sendCount);
        }
    }
}
