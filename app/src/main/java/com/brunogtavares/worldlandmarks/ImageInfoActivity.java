package com.brunogtavares.worldlandmarks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.brunogtavares.worldlandmarks.model.MyLandmark;
import com.brunogtavares.worldlandmarks.model.WikiEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageInfoActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<WikiEntry> {

    public static final String LANDMARK_BUNDLE_KEY = "LANDMARK_BUNDLE_KEY";
    private static final int WIKIENTRY_LOADER_ID = 1;

    @BindView(R.id.app_bar) AppBarLayout mAppBar;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_location_info) TextView mLocationInfo;
    @BindView(R.id.tv_wikipedia_content) TextView mWikipediaContent;
    @BindView(R.id.iv_image_info) ImageView mImageInfo;


    private MyLandmark mMyLandmark;
    private Uri mImageUri;
    private String mLandmarkName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_info);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        if(intent != null) {

            if(intent.hasExtra(LANDMARK_BUNDLE_KEY)) {
                mMyLandmark = intent.getParcelableExtra(LANDMARK_BUNDLE_KEY);
            }
            if(intent.hasExtra(MainActivity.URI_KEY)) {
                mImageUri = intent.getParcelableExtra(MainActivity.URI_KEY);
                mImageInfo.setImageURI(mImageUri);
            }
            mLandmarkName = mMyLandmark.getLandmark();

            loadViews();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void loadViews() {
        mToolbarLayout.setTitle(mMyLandmark.getLandmark());
        mLocationInfo.setText(mMyLandmark.getLocation());

        getLoaderManager().initLoader(WIKIENTRY_LOADER_ID, null, this);

    }

    @Override
    public android.content.Loader<WikiEntry> onCreateLoader(int id, Bundle args) {
        return new WikiEntryLoader(this, mLandmarkName);
    }

    @Override
    public void onLoadFinished(android.content.Loader<WikiEntry> loader, WikiEntry data) {

        mWikipediaContent.setText(data.getDescription());
    }

    @Override
    public void onLoaderReset(android.content.Loader<WikiEntry> loader) {

    }
}


