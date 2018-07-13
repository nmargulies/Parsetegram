package me.nmargulies.parsetegram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class HomeActivity extends AppCompatActivity implements CameraFragment.Callback, MyFragment.Callback, HomeFragment.HomeFragmentListener {

    RecyclerView rvPosts;
    Boolean onProfPicture;

    // how to launch the camera
    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;
    BottomNavigationView bottomNavigationView;


    Fragment fragment_home;
    CameraFragment fragment_camera;
    Fragment fragment_userdetails;
    Fragment fragment_my;
    ParseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // Set ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(getResources().getDimensionPixelSize(R.dimen.action_bar_elevation));

        currentUser = ParseUser.getCurrentUser();
        onProfPicture = false;

        // fragments
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragment_home = new HomeFragment();
        fragment_camera = new CameraFragment();
        fragment_my = new MyFragment();

        if (currentUser != null) {
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment_home).commit();

            bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_home:
                            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment_home).commit();
                            return true;
                        case R.id.action_camera:
                            onLaunchCamera();
                            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment_camera).commit();
                            return true;
                        case R.id.action_profile:
                            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment_my).commit();
                            return true;
                    }
                    return false;
                }
            });

            } else {
            // return to the Sign-In activity
            final Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void switchToUserDetailFragment(ParseUser user) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragment_userdetails = UserDetailsFragment.getNewInstance(user);
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment_userdetails).commit();
    }


    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(HomeActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (onProfPicture) {
                    // TODO - Upload the new avatar
                    String filepath = photoFile.getAbsolutePath();
                    final ParseFile newAvatar = new ParseFile(new File(filepath));
                    newAvatar.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("HomeActivity", "Avatar successfully uploaded.");

                                currentUser.put("profilePicture", newAvatar);
                                currentUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast toast = Toast.makeText(getApplicationContext(), "Profile Picture Changed!", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                            toast.show();
                                            onProfPicture = false;
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    fragment_camera.setSelectedFile(photoFile.getAbsolutePath());
                }
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPostCompleted() {
        final Fragment fragment_home = new HomeFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment_home).commit();
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    @Override
    public void onchangeProfPicture() {
        onProfPicture = true;
        onLaunchCamera();
    }

}
