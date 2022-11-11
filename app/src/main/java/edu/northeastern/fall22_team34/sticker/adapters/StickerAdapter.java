package edu.northeastern.fall22_team34.sticker.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.sticker.models.Sticker;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerViewHolder> {

    private List<Sticker> stickerReceived;
    private Context context;

    public StickerAdapter(List<Sticker> stickerReceived, Context context) {
        this.stickerReceived = stickerReceived;
        this.context = context;
    }

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_container_sticker, parent, false);
        return new StickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        if (stickerReceived != null) {
            Picasso.get().load(stickerReceived.get(position).imageUri).fit().centerCrop().into(holder.stickerImg);
            holder.stickerSender.setText("From: " + stickerReceived.get(position).sender);
            holder.stickerSentTime.setText(stickerReceived.get(position).timeSent);
        }
    }

    @Override
    public int getItemCount() {
        if (stickerReceived != null) {
            return stickerReceived.size();
        }
        return 0;
    }

    static class StickerViewHolder extends RecyclerView.ViewHolder{

        public ImageView stickerImg;
        public TextView stickerSender;
        public TextView stickerSentTime;

        public StickerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.stickerImg = itemView.findViewById(R.id.stickerImg);
            this.stickerSender = itemView.findViewById(R.id.stickerSender);
            this.stickerSentTime = itemView.findViewById(R.id.stickerSentTime);
        }
    }
}
