package com.koistorynew.ui.market.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {
    private List<String> imageUrls;
    private Context context;

    public ImageSliderAdapter(Context context) {
        this.context = context;
        this.imageUrls = new ArrayList<>();
    }

    public void setImageUrls(List<String> urls) {
        if (urls != null && !urls.isEmpty()) {
            this.imageUrls = urls;
        } else {
            this.imageUrls.clear(); // Clear the existing list if new list is empty
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return new ImageViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Ensure you are using the modulo operator to get the correct image URL
        String imageUrl = imageUrls.get(position % imageUrls.size());
        Glide.with(holder.imageView.getContext()).load(imageUrl).into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return imageUrls.isEmpty() ? 0 : Integer.MAX_VALUE; // For infinite scrolling, but only if there are images
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
        }
    }
}
