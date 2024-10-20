package com.koistorynew.ui.mymarket.adapter;

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
import com.koistorynew.ui.mymarket.model.MyMarket;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyMarketAdapter extends RecyclerView.Adapter<MyMarketAdapter.BlogViewHolder> {

    private List<MyMarket> postList;
    private Context context;

    public MyMarketAdapter(List<MyMarket> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyMarketAdapter.BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_mymarket, parent, false);
        return new MyMarketAdapter.BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMarketAdapter.BlogViewHolder holder, int position) {
        MyMarket product = postList.get(position);
        holder.nameTextView.setText(product.getArtName());
        holder.priceTextView.setText("$" + product.getPrice());
        Picasso.get().load(product.getImage()).into(holder.imageView);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MarketDetailsActivity.class);
            intent.putExtra("PRODUCT_NAME", product.getArtName());
            intent.putExtra("PRODUCT_DESCRIPTION", product.getDescription());
            intent.putExtra("PRODUCT_IMAGE", product.getImage());
            intent.putExtra("PRODUCT_PRICE", product.getPrice());
            //context.startActivity(intent);
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
