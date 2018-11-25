package com.brunogtavares.worldlandmarks;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
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
import com.brunogtavares.worldlandmarks.widget.WorldLandmarksWidget;
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

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ImageInfoActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<WikiEntry> {

    public static final String LANDMARK_BUNDLE_KEY = "LANDMARK_BUNDLE_KEY";
    private static final int WIKIENTRY_LOADER_ID = 1;
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

            loadViews();
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
        getLoaderManager().initLoader(WIKIENTRY_LOADER_ID, null, this);

    }

    @Override
    public android.content.Loader<WikiEntry> onCreateLoader(int id, Bundle args) {
        return new WikiEntryLoader(this, mLandmarkName);
    }

    @Override
    public void onLoadFinished(android.content.Loader<WikiEntry> loader, WikiEntry data) {
        displayContent();
        mWikipediaContent.setText(data.getDescription());
    }

    @Override
    public void onLoaderReset(android.content.Loader<WikiEntry> loader) {

    }

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

                    Toast.makeText(ImageInfoActivity.this, "Message Saved Sucessfully!", Toast.LENGTH_SHORT).show();

                    updateWidget();
                    goToMyLandmarksActivity();
                }
                else {
                    Toast.makeText(ImageInfoActivity.this, "Unable to download the file", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // TODO: Verify if user is logged in before display widget
        // TODO: Implment menu navigation including logout
        // TODO: Fix fab on MyLandmark detail activity
        // TODO: Add loading layout on MyLandmarks


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
                    Toast.makeText(ImageInfoActivity.this, "Successfully deleted from database!", Toast.LENGTH_SHORT).show();
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

    private void updateWidget() {
        Intent intent = new Intent(this, WorldLandmarksWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WorldLandmarksWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }


    // Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_get_landmark:
                Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
                startActivity(goToMainActivityIntent);
                return true;
            case R.id.action_my_landmarks:
                Intent goToLandmarksIntent = new Intent(this, MyLandmarksActivity.class);
                startActivity(goToLandmarksIntent);
                return true;
            case R.id.action_logout:
                signOut();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        mAuth.signOut();
        goToRegistrationActivity();
    }

    private void goToRegistrationActivity() {
        Intent registrationIntent = new Intent(this, EmailPasswordActivity.class);
        startActivity(registrationIntent);
        finish();
    }

}


