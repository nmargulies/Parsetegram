package me.nmargulies.parsetegram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.nmargulies.parsetegram.model.Post;

import static com.parse.Parse.getApplicationContext;

public class UserDetailsFragment extends Fragment {

    private ArrayList<Post> userPosts;
    RecyclerView rvMyPosts;
    MyPostsAdapter myPostsAdapter;
    ImageView ivProfile;

    ParseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_userdetails, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMyPosts = view.findViewById(R.id.rvUserPosts);

        TextView tvUsername = view.findViewById(R.id.tvUsername);
        tvUsername.setText(user.getUsername());
        ivProfile = view.findViewById(R.id.ivProfile);
        Glide.with(this).load(user.getParseFile("profilePicture").getUrl()).apply(RequestOptions.bitmapTransform(new RoundedCorners(250))).into(ivProfile);


        userPosts = new ArrayList<>();
        myPostsAdapter = new MyPostsAdapter(userPosts, getContext());
        rvMyPosts.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        rvMyPosts.setAdapter(myPostsAdapter);

        findUserPosts(user);
    }

    private void findUserPosts (ParseUser parseUser) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo("user", parseUser);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    userPosts.addAll(objects);
                    myPostsAdapter.notifyDataSetChanged();
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    public static Fragment getNewInstance(ParseUser user) {
        UserDetailsFragment fragment = new UserDetailsFragment();
        fragment.user = user;
        return fragment;
    }
}
