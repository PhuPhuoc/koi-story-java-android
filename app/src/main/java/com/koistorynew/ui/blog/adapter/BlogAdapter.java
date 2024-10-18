package com.koistorynew.ui.blog.adapter;

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
import com.koistorynew.ui.blog.BlogDetailsActivity;
import com.koistorynew.ui.blog.model.PostBlog;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    private List<PostBlog> postList;
    private Context context;

    public BlogAdapter(List<PostBlog> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_blog, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        PostBlog post = postList.get(position);
        holder.titleTextView.setText(post.getTitle());
        holder.authorTextView.setText(post.getAuthorName());
        // Picasso
        Picasso.get().load(post.getPostBlogImageUrl())
                .error(R.drawable.image_error)
                .into(holder.imageView);

        // Khi click vào card thì điều hướng sang trang chi tiết
        holder.itemView.setOnClickListener(view -> {
            // context ->  view.getContext()
            Intent intent = new Intent(view.getContext(), BlogDetailsActivity.class);
            intent.putExtra("BLOG_TITLE", post.getTitle());
            intent.putExtra("BLOG_AUTHOR", post.getAuthorName());
            intent.putExtra("BLOG_IMAGE", post.getPostBlogImageUrl());
            intent.putExtra("BLOG_CONTENT", post.getTitle());
            //context.startActivity(intent);
            view.getContext().startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, authorTextView;
        ImageView imageView;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.post_title);
            authorTextView = itemView.findViewById(R.id.post_author);
            imageView = itemView.findViewById(R.id.post_image);
        }
    }
}

