package com.koistorynew.ui.consult.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koistorynew.R;
import com.koistorynew.ui.consult.model.ConsultComment;
import com.koistorynew.ui.mymarket.adapter.CommentAdapter;
import com.koistorynew.ui.mymarket.model.PostMyMarketComment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ConsultCommentAdapter extends RecyclerView.Adapter<ConsultCommentAdapter.CommentViewHolder> {

    private List<ConsultComment> commentList;

    public ConsultCommentAdapter(List<ConsultComment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ConsultCommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new ConsultCommentAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultCommentAdapter.CommentViewHolder holder, int position) {
        ConsultComment comment = commentList.get(position);
        holder.userNameTextView.setText(comment.getUserName());
        holder.commentTextView.setText(comment.getCommentText());
        Picasso.get().load(comment.getAvatarUrl())
                .error(R.drawable.image_error)
                .into(holder.avatarImageView);

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, commentTextView;
        ImageView avatarImageView;  // Avatar ImageView

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.commentUserName);
            commentTextView = itemView.findViewById(R.id.commentText);
            avatarImageView = itemView.findViewById(R.id.commentAvatar);
        }
    }

}
