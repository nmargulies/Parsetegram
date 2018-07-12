package me.nmargulies.parsetegram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import me.nmargulies.parsetegram.model.Post;

public class EditActivity extends AppCompatActivity {
    String filepath;
    EditText descriptionInput;
    Bitmap takenImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        descriptionInput = findViewById(R.id.etCaption);
        filepath = getIntent().getStringExtra("filepath");

        // by this point we have the camera photo on disk
        takenImage = BitmapFactory.decodeFile(filepath);

        // RESIZE BITMAP, see section below
        // Load the taken image into a preview
        ImageView ivPreview = (ImageView) findViewById(R.id.ivPreview);
        ivPreview.setImageBitmap(takenImage);
    }

    public void onPost(View view) {

        final String description = descriptionInput.getText().toString();
        final ParseUser user = ParseUser.getCurrentUser();
        final File file = new File(filepath);
        final ParseFile parseFile = new ParseFile(file);

        parseFile.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("HomeActivity", "Save file success.");
                    createPost(description, parseFile, user);
                    finish();

                } else {
                    e.printStackTrace();
                }
            }
        });

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
}
