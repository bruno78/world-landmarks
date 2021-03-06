package com.brunogtavares.worldlandmarks;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brunogtavares.worldlandmarks.Firebase.FirebaseEntry;
import com.brunogtavares.worldlandmarks.model.MyLandmark;
import com.brunogtavares.worldlandmarks.model.WikiEntry;
import com.brunogtavares.worldlandmarks.utils.BitmapUtils;
import com.brunogtavares.worldlandmarks.utils.NetworkUtils;
import com.brunogtavares.worldlandmarks.utils.WikipediaAPIUtils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ImageInfoActivity extends AppCompatActivity {

    public static final String LANDMARK_BUNDLE_KEY = "LANDMARK_BUNDLE_KEY";
    // private static final int WIKIENTRY_LOADER_ID = 1;
    private static final String DB_ENTRY_KEY = "DB_ENTRY_KEY";

    @BindView(R.id.app_bar) AppBarLayout mAppBar;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_location_info) TextView mLocationInfo;
    @BindView(R.id.tv_wikipedia_content) TextView mWikipediaContent;
    @BindView(R.id.iv_image_info) ImageView mImageInfo;
    @BindView(R.id.rl_loading_imageinfo_layout) RelativeLayout mLoadingScreen;
    @BindView(R.id.fab) FloatingActionButton mFabButton;

    private MyLandmark mMyLandmark;
    private Uri mImageUri;
    private String mLandmarkName;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDb;
    private StorageReference mFirebaseStorage;

    private String mUserId;
    private String mKey;
    private String mFirebaseFileImagePath;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_info);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null) goToRegistrationActivity();

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        if(savedInstanceState != null && savedInstanceState.containsKey(DB_ENTRY_KEY)) {
            mKey = savedInstanceState.getString(DB_ENTRY_KEY);
            mFabButton.setSelected(true);
        }


        Intent intent = getIntent();
        if(intent != null) {

            if(intent.hasExtra(LANDMARK_BUNDLE_KEY)) {
                mMyLandmark = intent.getParcelableExtra(LANDMARK_BUNDLE_KEY);
            }
            if(intent.hasExtra(MainActivity.URI_KEY)) {
                mImageUri = intent.getParcelableExtra(MainActivity.URI_KEY);
                Glide.with(this).load(mImageUri).into(mImageInfo);
            }
            mLandmarkName = mMyLandmark.getLandmarkName();

            boolean isConnected = NetworkUtils.checkForNetworkStatus(this);
            if(isConnected) {
                loadViews();
            }
            else {
                Toast.makeText(this, R.string.check_connection, Toast.LENGTH_SHORT).show();
                Intent checkNetworkIntent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                startActivity(checkNetworkIntent);
            }

        }

        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();
        mUserDb = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEntry.TABLE_USERS).child(mUserId)
                .child(FirebaseEntry.TABLE_MYLANDMARKS);
        mFirebaseStorage = FirebaseStorage.getInstance().getReference()
                .child(mUserId);


        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFabButton.setSelected(!mFabButton.isSelected());

                int imageResource = mFabButton.isSelected()? R.drawable.ic_check_icon :
                        R.drawable.ic_add_icon;

                changeFab(imageResource);

                if(mFabButton.isSelected()) {
                    saveInfo();
                }
                else {
                    deleteInfo();
                }

            }
        });
    }

    /***
     * This method solves this problem:
     * https://stackoverflow.com/questions/52133846/fab-icon-disappers-after-click
     * https://stackoverflow.com/questions/49587945/setimageresourceint-doesnt-work-after-setbackgroundtintcolorlistcolorstateli/52158081#52158081
     * @param resource
     */
    private void changeFab(int resource) {
        mFabButton.hide();
        mFabButton.setImageResource(resource);
        mFabButton.show();
    }

    private void loadViews() {
        mToolbarLayout.setTitle(mMyLandmark.getLandmarkName());
        mLocationInfo.setText(mMyLandmark.getLocation());
        displayLoading();
        // getLoaderManager().initLoader(WIKIENTRY_LOADER_ID, null, this);

        WikiEntryAsyncTask task = new WikiEntryAsyncTask();
        task.execute(mLandmarkName);
    }

    // This is only commented out because I'll be using AsyncTask for submission.
//    @Override
//    public android.content.Loader<WikiEntry> onCreateLoader(int id, Bundle args) {
//        return new WikiEntryLoader(this, mLandmarkName);
//    }
//
//    @Override
//    public void onLoadFinished(android.content.Loader<WikiEntry> loader, WikiEntry data) {
//        displayContent();
//        mWikipediaContent.setText(data.getDescription());
//    }
//
//    @Override
//    public void onLoaderReset(android.content.Loader<WikiEntry> loader) {
//
//    }

    private void saveInfo() {
        String landmark = mMyLandmark.getLandmarkName();
        String location = mMyLandmark.getLocation();
        String confidence = mMyLandmark.getConfidence();
        String latitude = mMyLandmark.getLatitude();
        String longitude = mMyLandmark.getLongitude();
        String description = mWikipediaContent.getText().toString();


        // Get a reference with an empty spot so an ID can be retrieved
        final DatabaseReference newEntry = mUserDb.push();
        mKey = newEntry.getKey();

        final Map imageInfo = new HashMap<String, String>();
        imageInfo.put(FirebaseEntry.LANDMARK_ID, mKey);
        imageInfo.put(FirebaseEntry.LANDMARK_NAME, landmark);
        imageInfo.put(FirebaseEntry.LOCATION, location);
        imageInfo.put(FirebaseEntry.CONFIDENCE, confidence);
        imageInfo.put(FirebaseEntry.LATITUDE, latitude);
        imageInfo.put(FirebaseEntry.LONGITUDE, longitude);
        imageInfo.put(FirebaseEntry.DESCRIPTION, description);

        newEntry.setValue(imageInfo);
        mFirebaseFileImagePath = BitmapUtils.createFileName();
        final StorageReference newStorageEntry = mFirebaseStorage.child(mFirebaseFileImagePath);

        byte[] data = BitmapUtils.getUploadTask(mImageUri, getApplication());
        UploadTask uploadTask = newStorageEntry.putBytes(data);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()) {
                    Timber.d("Something went wrong!");
                }

                // Continue with the task ato get the download URL
                return newStorageEntry.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {

                    imageInfo.put(FirebaseEntry.IMAGE_URI, mFirebaseFileImagePath);
                    newEntry.updateChildren(imageInfo);

                    Toast.makeText(ImageInfoActivity.this, R.string.message_save_success, Toast.LENGTH_SHORT).show();

                    goToMyLandmarksActivity();
                }
                else {
                    Toast.makeText(ImageInfoActivity.this, R.string.message_save_failure, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void goToMyLandmarksActivity() {
        Intent myLandmarksIntent = new Intent(this, MyLandmarksActivity.class);
        startActivity(myLandmarksIntent);
        finish();
    }

    private void deleteInfo() {
        if(mKey != null) {
            mUserDb.child(mKey).setValue(null);
            mFirebaseStorage.child(mFirebaseFileImagePath).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ImageInfoActivity.this, R.string.database_delete_success, Toast.LENGTH_SHORT).show();
                    mKey = null;
                }
            });
        }
    }

    private void displayContent() {
        mWikipediaContent.setVisibility(View.VISIBLE);
        mLoadingScreen.setVisibility(View.GONE);
    }

    private void displayLoading() {
        mLoadingScreen.setVisibility(View.VISIBLE);
        mWikipediaContent.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(DB_ENTRY_KEY, mKey);
    }


    // Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_get_landmark:
                Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(goToMainActivityIntent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
                else {
                    startActivity(goToMainActivityIntent);
                }
                return true;
            case R.id.action_my_landmarks:
                Intent goToLandmarksIntent = new Intent(this, MyLandmarksActivity.class);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(goToLandmarksIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else {
                    startActivity(goToLandmarksIntent);
                }
                return true;
            case R.id.action_logout:
                signOut();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_logout)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        goToRegistrationActivity();
                    }})
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    private void goToRegistrationActivity() {
        Intent registrationIntent = new Intent(this, EmailPasswordActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(registrationIntent, bundle);
            finish();
        } else {
            startActivity(registrationIntent);
            finish();
        }

    }

    private class WikiEntryAsyncTask extends AsyncTask<String, Void, WikiEntry> {
        @Override
        protected WikiEntry doInBackground(String... strings) {
            if(strings[0]  == null) return null;
            WikiEntry wikiEntry = new WikiEntry();

            try {
                wikiEntry = WikipediaAPIUtils.extractWikiEntry(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return wikiEntry;
        }

        @Override
        protected void onPostExecute(WikiEntry wikiEntry) {
            super.onPostExecute(wikiEntry);
            mWikipediaContent.setText(wikiEntry.getDescription());
            displayContent();
        }
    }

}


