package me.nmargulies.parsetegram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.nmargulies.parsetegram.model.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> posts;
    Context context;
    private PostsListener listener;

    public interface PostsListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered
        void onProfileClicked(ParseUser user);
    }


    public PostAdapter(FragmentActivity activity, ArrayList<Post> posts, PostsListener listener) {
        this.posts = posts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.item_post, parent, false);
        final ViewHolder viewHolder = new ViewHolder(postView);


        viewHolder.buttonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Post post = posts.get(position);
                post.setFavoriteCount(post.getFavoriteCount() + 1);
                viewHolder.buttonFavorite.setBackground(ContextCompat.getDrawable(context, R.drawable.ufi_heart_full));
                viewHolder.tvFavoriteCount.setText(post.getFavoriteCount().toString() + " likes");
            }
        });

        viewHolder.ivProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                Post post = posts.get(position);

                listener.onProfileClicked(post.getUser());

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Post post = posts.get(position);

        viewHolder.tvDescription.setText(post.getUser().getUsername() + ":   " + post.getDescription());
        viewHolder.tvUsername.setText(post.getUser().getUsername());
        viewHolder.tvDate.setText(post.getDate(post));
        viewHolder.tvFavoriteCount.setText(post.getFavoriteCount().toString() + " likes");
        Glide.with(context).load(post.getUser().getParseFile("profilePicture").getUrl()).apply(RequestOptions.bitmapTransform(new RoundedCorners(500))).into(viewHolder.ivProfile);
        Glide.with(context).load(post.getImage().getUrl()).into(viewHolder.ivPost);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivPicture) ImageView ivPost;
        @Nullable @BindView(R.id.ivProfile) ImageButton ivProfile;
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvDescription) TextView tvDescription;
        @BindView(R.id.buttonFavorite) ImageButton buttonFavorite;
        @BindView(R.id.tvFavorite) TextView tvFavoriteCount;
        @BindView(R.id.tvDate) TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}
