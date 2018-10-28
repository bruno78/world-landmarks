package com.brunogtavares.worldlandmarks;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brunogtavares.worldlandmarks.model.MyLandmark;
import com.brunogtavares.worldlandmarks.model.WikiEntry;
import com.bumptech.glide.Glide;

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
    @BindView(R.id.rl_loading_imageinfo_layout) RelativeLayout mLoadingScreen;
    @BindView(R.id.fab) FloatingActionButton mFabButton;

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
                Glide.with(this).load(mImageUri).into(mImageInfo);
            }
            mLandmarkName = mMyLandmark.getLandmark();

            loadViews();
        }


        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//
//                fab.setSelected(!fab.isSelected());
//                fab.setImageResource(fab.isSelected() ? R.drawable.animated_plus : R.drawable.animated_check);
//                Drawable drawable = fab.getDrawable();
//                if (drawable instanceof Animatable) {
//                    ((Animatable) drawable).start();
//                }
                // TODO: Make sure SELECTED is member variable
                // Change the background color on save
                // save to firebase
                mFabButton.setSelected(!mFabButton.isSelected());
                int imageResource = mFabButton.isSelected()? R.drawable.ic_check_icon :
                        R.drawable.ic_add_icon;

                changeFab(imageResource);


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
        mToolbarLayout.setTitle(mMyLandmark.getLandmark());
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

    private void displayContent() {
        mWikipediaContent.setVisibility(View.VISIBLE);
        mLoadingScreen.setVisibility(View.GONE);
    }

    private void displayLoading() {
        mLoadingScreen.setVisibility(View.VISIBLE);
        mWikipediaContent.setVisibility(View.GONE);
    }
}


