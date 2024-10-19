package com.koistorynew.ui.market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koistorynew.R;
import com.koistorynew.ui.market.model.PostMarket;

import java.util.List;

public class MarketAdapter  extends RecyclerView.Adapter<MarketAdapter.BlogViewHolder> {
    private List<PostMarket> postList;
    private Context context;

    public MarketAdapter(List<PostMarket> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public MarketAdapter.BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_market, parent, false);
        return new MarketAdapter.BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
