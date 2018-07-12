package me.nmargulies.parsetegram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import me.nmargulies.parsetegram.model.Post;

public class CameraFragment extends Fragment {

    EditText descriptionInput;
    Bitmap takenImage;
    Button postButton;
    ImageView ivPreview;

    ParseFile parseFile;


    interface Callback {
        void onPostCompleted();
    }
    private Callback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Callback) {

            // If it is an instance of our Callback then we want to cast the context to a Callback
            // and store it as a reference so we can later update the callback when there has been
            // a text change event.
            callback = (Callback) context;
        } else {
            // Throwing an error and making your application crash instead of just sweeping it under
            // the rug is called being an "offensive" programmer.
            //
            // The best defense is a strong offense.
            throw new IllegalStateException("Containing context must implement UserInputFragment.Callback.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_camera, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        descriptionInput = view.findViewById(R.id.etCaption);

        // RESIZE BITMAP, see section below
        // Load the taken image into a preview
        ivPreview = view.findViewById(R.id.ivPreview);
        ivPreview.setBackgroundColor(Color.BLACK);

        postButton = view.findViewById(R.id.postButton);
        postButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final String description = descriptionInput.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();

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
    }

    public void setSelectedFile(String filepath) {
        Bitmap previewBitmap = BitmapFactory.decodeFile(filepath);
        ivPreview.setImageBitmap(previewBitmap);

        parseFile = new ParseFile(new File(filepath));
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
                    callback.onPostCompleted();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}
