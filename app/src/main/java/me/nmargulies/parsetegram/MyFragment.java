package me.nmargulies.parsetegram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class MyFragment extends Fragment {

    private final ArrayList<Post> myPosts = new ArrayList<>();
    Button logoutButton;
    Button profilePictureButton;
    ImageView ivProfile;
    ParseUser currentUser;
    RecyclerView rvMyPosts;
    MyPostsAdapter myPostsAdapter;

    interface Callback {
        void onchangeProfPicture();
    }

    private MyFragment.Callback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MyFragment.Callback) {
            callback = (MyFragment.Callback) context;
        } else {
            throw new IllegalStateException("Containing context must implement UserInputFragment.Callback.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = ParseUser.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_my, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvUsername = view.findViewById(R.id.tvUsername);
        tvUsername.setText(currentUser.getUsername());

        populatePosts();
        rvMyPosts = view.findViewById(R.id.rvMyPosts);
        myPostsAdapter = new MyPostsAdapter(myPosts, getContext());
        rvMyPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvMyPosts.setAdapter(myPostsAdapter);

        ivProfile = view.findViewById(R.id.ivProfile);
        Glide.with(this).load(currentUser.getParseFile("profilePicture").getUrl()).apply(RequestOptions.bitmapTransform(new RoundedCorners(250))).into(ivProfile);

        logoutButton = view.findViewById(R.id.logoutBtn);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        profilePictureButton = view.findViewById(R.id.profilePictureBtn);
        profilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onchangeProfPicture();
            }
        });
    }

    private void populatePosts () {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo("user", currentUser);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Post item = objects.get(i);
                        Log.d("posts", "item");
                        myPosts.add(item);
                        myPostsAdapter.notifyItemInserted(myPosts.size() - 1);

                    }
                } else {
                        Log.d("item", "Error: " + e.getMessage());
                    }
                }
        });

    }
}
