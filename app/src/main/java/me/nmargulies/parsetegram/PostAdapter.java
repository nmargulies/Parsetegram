package me.nmargulies.parsetegram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.nmargulies.parsetegram.model.Post;
import me.nmargulies.parsetegram.model.User;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> posts;
    Context context;

    public PostAdapter(ArrayList<Post> posts) { this.posts = posts; }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.item_post, parent, false);
        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Post post = posts.get(position);

        viewHolder.tvDescription.setText(post.getDescription());
        viewHolder.tvUsername.setText(post.getUser().getUsername());
        Glide.with(context).load(post.getUser().getParseFile("profilePicture").getUrl()).into(viewHolder.ivProfile);
        Glide.with(context).load(post.getImage().getUrl()).into(viewHolder.ivPost);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivPicture) ImageView ivPost;
        @Nullable @BindView(R.id.ivProfile) ImageView ivProfile;
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvDescription) TextView tvDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
