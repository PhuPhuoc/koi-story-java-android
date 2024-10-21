package com.koistorynew.ui.consult.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koistorynew.R;
import com.koistorynew.ui.consult.ConsultCommentActivity;
import com.koistorynew.ui.consult.model.Consult;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ConsultAdapter extends RecyclerView.Adapter<ConsultAdapter.ConsultViewHolder> {
    private List<Consult> consultList;
    private Context context;

    public ConsultAdapter(List<Consult> consultList) {
        this.consultList = consultList;
    }

    public void updateData(List<Consult> newConsultList) {
        this.consultList = newConsultList;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public ConsultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consult, parent, false);
        return new ConsultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultViewHolder holder, int position) {
        Consult consult = consultList.get(position);
        holder.nameTextView.setText(consult.getUser_name());
        holder.datePostedTextView.setText(consult.getCreated_at());
        holder.title.setText(consult.getTitle());
        Picasso.get().load(consult.getUser_avatar()).into(holder.avatar);
        holder.descriptionTextView.setText(consult.getContent());
        Picasso.get().load(consult.getFile_path()).into(holder.imageView);

        holder.commentButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ConsultCommentActivity.class);
            intent.putExtra("POST_ID", consult.getId()); // Assuming you want to pass consult ID
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return consultList.size();
    }

    public static class ConsultViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageView avatar;
        public TextView nameTextView;
        public TextView datePostedTextView;
        public TextView descriptionTextView;
        public TextView title;
        public Button commentButton; // Add this

        public ConsultViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            avatar = itemView.findViewById(R.id.avatarImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            datePostedTextView = itemView.findViewById(R.id.datePostedTextView);
            title = itemView.findViewById(R.id.consultTitle);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView); // Initialize this
            commentButton = itemView.findViewById(R.id.commentButton); // Initialize this
        }
    }
}
