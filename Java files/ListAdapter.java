package com.example.familyshare;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private  timeListener listener;
    private Context context;
    private List<Post> items;

    public ListAdapter(Context context, List<Post> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_layout,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, int position) {
        final int posRevers = items.size() - (position + 1);
        final Post item = items.get(posRevers);

        holder.description.setText(item.getDescription());

        Glide.with(context)
                .load(item.getPostImageUrl())
                .fitCenter()
                .placeholder(R.drawable.noimage)
                .into(holder.image);

        holder.username.setText("By: @"+item.getUsername());
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder{

        private ImageView image;
        private TextView description;
        private TextView username;
        private Button comments;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.postimage);
            description = itemView.findViewById(R.id.desc);
            username = itemView.findViewById(R.id.username);
            comments = itemView.findViewById(R.id.comment);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                }
            });
        }
    }

    public interface timeListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void onCommentCLick(DocumentSnapshot documentSnapshot, int position);
    }

    public void settimeListener(timeListener listener){
        this.listener = listener;
    }
}

