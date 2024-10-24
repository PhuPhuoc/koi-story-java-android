package com.koistorynew.ui.mymarket.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koistorynew.R;
import com.koistorynew.ui.mymarket.EditImagesActivity;
import com.koistorynew.ui.mymarket.EditMarketActivity;
import com.koistorynew.ui.mymarket.MyMarketDetailActivity;
import com.koistorynew.ui.mymarket.model.MyMarket;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyMarketAdapter extends RecyclerView.Adapter<MyMarketAdapter.BlogViewHolder> {

    private List<MyMarket> postList;
    private Context context;
    private final OnDeleteClickListener onDeleteClickListener; // Thêm interface

    public MyMarketAdapter(Context context, List<MyMarket> postList, OnDeleteClickListener onDeleteClickListener) {
        this.context = context; // Initialize context
        this.postList = postList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void updateData(List<MyMarket> newPostMarketList) {
        this.postList = newPostMarketList;
        notifyDataSetChanged();
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
        String imageUrl = product.getImage();

        // Kiểm tra nếu URL không hợp lệ hoặc rỗng
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            // Nếu URL trống hoặc null, bạn có thể đặt một hình ảnh mặc định
            holder.imageView.setImageResource(R.drawable.ic_no_image);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MyMarketDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            view.getContext().startActivity(intent);
        });

        // Gọi phương thức xóa từ listener
        holder.deleteButton.setOnClickListener(v -> {
            onDeleteClickListener.onDeleteClick(product.getId());
        });

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditMarketActivity.class);
            intent.putExtra("ID", product.getId());
            context.startActivity(intent);
        });
        holder.editImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditImagesActivity.class);
            intent.putExtra("ID", product.getId());
            context.startActivity(intent);
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
        public Button deleteButton; // Thêm ImageView cho nút xóa
        public Button editButton;
        public Button editImageButton;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
            editImageButton = itemView.findViewById(R.id.editImageButton);
        }
    }

    // Interface để xử lý sự kiện xóa
    public interface OnDeleteClickListener {
        void onDeleteClick(String itemId);
    }

}
