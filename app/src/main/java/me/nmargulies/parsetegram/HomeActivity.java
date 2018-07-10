package me.nmargulies.parsetegram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

import me.nmargulies.parsetegram.model.Post;

public class HomeActivity extends AppCompatActivity {

    private static final String imagePath = "/sdcard/DCIM/Camera/IMG_20180709_160522.jpg";
    private EditText descriptionInput;
    private Button createButton;
    private Button refreshButton;
    private Button logoutButton;
    private Button cameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            descriptionInput = findViewById(R.id.description_et);
            createButton = findViewById(R.id.create_btn);
            refreshButton = findViewById(R.id.refresh_btn);
            logoutButton = findViewById(R.id.logoutbtn);

            createButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    final String description = descriptionInput.getText().toString();
                    final ParseUser user = ParseUser.getCurrentUser();

                    //TODO -- take photo from camera and pass into it
                    final File file = new File(imagePath);
                    final ParseFile parseFile = new ParseFile(file);

                    parseFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("HomeActivity", "Save file success.");
                                createPost(description, parseFile, user);
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadTopPosts();
                }
            });

            logoutButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    ParseUser.logOut();
                    ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                    finish();
                }
            });


        } else {
            // return to the signin activity
            final Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void createPost(String description, ParseFile imageFile, ParseUser user) {
        final Post newPost = new Post();
        newPost.setDescription(description);
        newPost.setImage(imageFile);
        newPost.setUser(user);

        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("HomeActivity", "Create post success!");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadTopPosts() {
        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();

        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Log.d("HomeActivity", "Post[" + i + "] = "
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername()
                        );
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
