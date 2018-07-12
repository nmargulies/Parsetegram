package me.nmargulies.parsetegram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.nmargulies.parsetegram.model.Post;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.ViewHolder> {

    List<Post> myPosts;
    Context context;

    @NonNull
    @Override
    public MyPostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.item_mypost, parent, false);
        final ViewHolder viewHolder = new MyPostsAdapter.ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostsAdapter.ViewHolder viewHolder, int position) {
        Post myPost = myPosts.get(position);

        Glide.with(context).load(myPost.getImage().getUrl()).into(viewHolder.ivMyPost);
    }

    @Override
    public int getItemCount() {
        return myPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivMyPost) ImageView ivMyPost;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
