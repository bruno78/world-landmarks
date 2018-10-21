package com.brunogtavares.worldlandmarks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.brunogtavares.worldlandmarks.model.MyLandmark;

public class ImageInfoActivity extends AppCompatActivity {

    public static final String LANDMARK_BUNDLE_KEY = "LANDMARK_BUNDLE_KEY";

    private MyLandmark mMyLandmark;
    private Uri mImageUri;
    private Bitmap mImageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if(intent != null) {

            if(intent.hasExtra(LANDMARK_BUNDLE_KEY)) {
                mMyLandmark = intent.getParcelableExtra(LANDMARK_BUNDLE_KEY);
            }
            if(intent.hasExtra(MainActivity.URI_KEY)) {
                mImageUri = Uri.parse(intent.getStringExtra(MainActivity.URI_KEY));
            }
            if(intent.hasExtra(MainActivity.BITMAP_KEY)) {
                mImageBitmap = intent.getParcelableExtra(MainActivity.BITMAP_KEY);
            }
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

}
