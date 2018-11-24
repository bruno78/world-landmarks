package com.brunogtavares.worldlandmarks;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brunogtavares.worldlandmarks.Firebase.FirebaseEntry;
import com.brunogtavares.worldlandmarks.model.MyLandmark;
import com.brunogtavares.worldlandmarks.widget.WorldLandmarksWidget;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyLandmarkDetailActivity extends AppCompatActivity {

    public static final String MY_LANDMARK_KEY = "MY_LANDMARK_KEY";


    @BindView(R.id.tl_mylandmark_detail_toolbar_layout) CollapsingToolbarLayout mToolBarLayout;
    @BindView(R.id.iv_mylandmark_detail_image) ImageView mImageView;
    @BindView(R.id.tv_mylandmark_detail_location) TextView mLocation;
    @BindView(R.id.tv_mylandmark_detail_info) TextView mInfo;
    @BindView(R.id.rl_loading_mylandmark_detail_layout) RelativeLayout mLoadingScreen;

    MyLandmark mMyLandmark;

    String mUserId;
    DatabaseReference mUserDb;
    StorageReference mImageStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_landmark_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        displayLoading();

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserDb = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEntry.TABLE_USERS).child(mUserId)
                .child(FirebaseEntry.TABLE_MYLANDMARKS);
        mImageStorage = FirebaseStorage.getInstance().getReference().child(mUserId);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(MY_LANDMARK_KEY)) {
            mMyLandmark = intent.getParcelableExtra(MY_LANDMARK_KEY);
            populateView();
            displayContent();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_mylandmark_detail);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDeleteDialog();
            }
        });
    }

    private void populateView() {

        mToolBarLayout.setTitle(mMyLandmark.getLandmarkName());
        mImageStorage.child(mMyLandmark.getImageUri()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri)
                        .into(mImageView);
            }
        });
        mLocation.setText(mMyLandmark.getLocation());
        mInfo.setText(mMyLandmark.getDescription());

    }

    private void alertDeleteDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete_my_landmark)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMyLandmark();
                    }})
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();

    }
    private void deleteMyLandmark() {

        mUserDb.child(mMyLandmark.getId()).setValue(null);
        mImageStorage.child(mMyLandmark.getImageUri()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MyLandmarkDetailActivity.this, "Successfully deleted from database!", Toast.LENGTH_SHORT).show();
                updateWidget();
                goToMyLandmarksActivity();
            }
        });
    }

    private void goToMyLandmarksActivity() {
        Intent intent = new Intent(this, MyLandmarksActivity.class);
        startActivity(intent);
        finish();
    }

    private void displayContent() {
        mInfo.setVisibility(View.VISIBLE);
        mLoadingScreen.setVisibility(View.GONE);
    }

    private void displayLoading() {
        mLoadingScreen.setVisibility(View.VISIBLE);
        mInfo.setVisibility(View.GONE);
    }

    private void updateWidget() {
        Intent intent = new Intent(this, WorldLandmarksWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WorldLandmarksWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }
}
