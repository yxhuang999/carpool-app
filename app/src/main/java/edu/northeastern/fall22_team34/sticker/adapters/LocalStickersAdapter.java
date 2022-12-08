package edu.northeastern.fall22_team34.sticker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.sticker.LocalStickerOnClickListener;

public class LocalStickersAdapter extends RecyclerView.Adapter<LocalStickersAdapter.LocalStickerViewHolder> {

    private List<Integer> stickerFileNames;
    private Context context;

    private LocalStickerOnClickListener onClickListener;

    public LocalStickersAdapter(List<Integer> stickerFileNames, Context context, LocalStickerOnClickListener onClickListener) {
        this.stickerFileNames = stickerFileNames;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public LocalStickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_container_stickers_local_stickers, parent, false);
        return new LocalStickerViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalStickerViewHolder holder, int position) {
        holder.localStickerImg.setImageResource(stickerFileNames.get(position));
    }

    @Override
    public int getItemCount() {
        return stickerFileNames.size();
    }


    static class LocalStickerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView localStickerImg;

        public LocalStickerOnClickListener onClickListener;

        public LocalStickerViewHolder(@NonNull View itemView, LocalStickerOnClickListener onClickListener) {
            super(itemView);
            this.localStickerImg = itemView.findViewById(R.id.localStickerImg);
            this.onClickListener = onClickListener;

            localStickerImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onStickerClick(getAdapterPosition());
        }
    }
}
