package com.koistorynew.ui.market.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koistorynew.R;
import com.koistorynew.ui.market.MarketDetailsActivity;
import com.koistorynew.ui.market.model.PostMarket;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.BlogViewHolder> {
    private List<PostMarket> postList;
    private Context context;

    public MarketAdapter(List<PostMarket> postList) {
        this.postList = postList;
    }

    public void updateData(List<PostMarket> newPostMarketList) {
        this.postList = newPostMarketList;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public MarketAdapter.BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_market, parent, false);
        return new MarketAdapter.BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        PostMarket product = postList.get(position);
        holder.nameTextView.setText(product.getArtName());
        holder.priceTextView.setText("" + product.getPrice());
        String imageUrl = product.getImage();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .resize(300, 300)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_no_image);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MarketDetailsActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            view.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameTextView;
        public TextView priceTextView;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }
}
