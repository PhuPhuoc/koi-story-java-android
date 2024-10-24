package com.koistorynew.ui.myconsult.adapter;

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
import com.koistorynew.ui.myconsult.EditConsultActivity;
import com.koistorynew.ui.myconsult.EditImagesConsultActivity;
import com.koistorynew.ui.myconsult.MyConsultCommentActivity;
import com.koistorynew.ui.myconsult.model.MyConsult;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyConsultAdapter extends RecyclerView.Adapter<MyConsultAdapter.ConsultViewHolder> {
    private List<MyConsult> consultList;
    private Context context;
    private final MyConsultAdapter.OnDeleteClickListener onDeleteClickListener;

    public MyConsultAdapter(Context context, List<MyConsult> consultList, MyConsultAdapter.OnDeleteClickListener onDeleteClickListener) {
        this.consultList = consultList;
        this.context = context;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void updateData(List<MyConsult> newConsultList) {
        this.consultList = newConsultList;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public MyConsultAdapter.ConsultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_consult, parent, false);
        return new MyConsultAdapter.ConsultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyConsultAdapter.ConsultViewHolder holder, int position) {
        MyConsult consult = consultList.get(position);
        holder.nameTextView.setText(consult.getUser_name());
        holder.datePostedTextView.setText(consult.getCreated_at());
        holder.descriptionTextView.setText(consult.getContent());
        String imageUrl = consult.getFile_path();
        holder.title.setText(consult.getTitle());

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .resize(300, 300)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_no_image);
        }

        holder.deleteButton.setOnClickListener(v -> {
            onDeleteClickListener.onDeleteClick(consult.getId());
        });

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditConsultActivity.class);
            intent.putExtra("CONSULT_ID", consult.getId());
            intent.putExtra("CONSULT_TITLE", consult.getTitle());
            intent.putExtra("CONSULT_CONTENT", consult.getContent());
            intent.putExtra("CONSULT_POST_TYPE", consult.getPost_type());
            intent.putExtra("CONSULT_FILE_PATH", consult.getFile_path());
            context.startActivity(intent);
        });

        holder.commentButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MyConsultCommentActivity.class);
            intent.putExtra("POST_ID", consult.getId());
            view.getContext().startActivity(intent);
        });

        holder.editImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditImagesConsultActivity.class);
            intent.putExtra("ID", consult.getId());
            intent.putExtra("IMAGE", consult.getFile_path());
            intent.putExtra("IMAGE_ID", consult.getImage_id());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return consultList.size();
    }

    public static class ConsultViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameTextView;
        public TextView datePostedTextView; // Add this
        public TextView descriptionTextView; // Add this
        public Button commentButton; // Add this
        public Button deleteButton;
        public Button editButton;
        public TextView title;
        public Button editImageButton;

        public ConsultViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            datePostedTextView = itemView.findViewById(R.id.datePostedTextView); // Initialize this
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView); // Initialize this
            commentButton = itemView.findViewById(R.id.commentButton); // Initialize this
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
            title = itemView.findViewById(R.id.consultTitle);
            editImageButton = itemView.findViewById(R.id.editImageButton);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String itemId);
    }
}
