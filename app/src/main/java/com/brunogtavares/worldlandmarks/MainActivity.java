package com.brunogtavares.worldlandmarks;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brunogtavares.worldlandmarks.model.MyLandmark;
import com.brunogtavares.worldlandmarks.utils.BitmapUtils;
import com.brunogtavares.worldlandmarks.utils.NetworkUtils;
import com.brunogtavares.worldlandmarks.utils.PermissionUtils;
import com.brunogtavares.worldlandmarks.utils.LandmarkUtils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmarkDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.brunogtavares.worldlandmarks.utils.PermissionUtils.*;


/**
 * https://firebase.google.com/docs/ml-kit/android/recognize-landmarks
 * Using File Provider to capture image:
 * https://android.jlelse.eu/androids-new-image-capture-from-a-camera-using-file-provider-dd178519a954
 */
public class MainActivity extends AppCompatActivity {

    private static final String RESULT_KEY = "RESULT_KEY";
    public static final String URI_KEY = "URI_KEY";
    private static final int REQUEST_CAPTURE_IMAGE =  100;

    @BindView(R.id.iv_image) ImageView mImage;
    @BindView(R.id.tv_not_found) TextView mNotFoundText;
    @BindView(R.id.fab_clear) FloatingActionButton mClearButton;
    @BindView(R.id.bt_choose_image) Button mGetImageButton;

    @BindView(R.id.ll_result_list_item) LinearLayout mResultInfo;
    @BindView(R.id.tv_landmark_name) TextView mLandmark;
    @BindView(R.id.tv_location) TextView mPlace;
    @BindView(R.id.tv_confidence_value) TextView mConfidence;
    @BindView(R.id.rl_loading_layout) RelativeLayout mLoadingLayout;

    private Uri mImageUri;
    private Context mContext;
    private MyLandmark mMyLandmark;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null) goToRegistrationActivity();

        // Planting Timber
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());

        ButterKnife.bind(this);

        mContext = this;

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(URI_KEY)) {
                mImageUri = savedInstanceState.getParcelable(URI_KEY);
                displayImage();
            }
            if(savedInstanceState.containsKey(RESULT_KEY)) {
                mMyLandmark = savedInstanceState.getParcelable(RESULT_KEY);
                setViews(mMyLandmark);
                displayInfo();
            }
        }
        else {
            displayInitialState();
        }

        // Check for network connection
        boolean isConnected = NetworkUtils.checkForNetworkStatus(this);
        if(isConnected) {
            mGetImageButton.setEnabled(isConnected);
        }
        else {
            mGetImageButton.setEnabled(isConnected);
            Toast.makeText(this, R.string.check_connection, Toast.LENGTH_SHORT).show();
        }

        mGetImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(com.brunogtavares.worldlandmarks.MainActivity.this);
                builder.setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startGalleryChooser();
                                    }
                                })
                        .setNegativeButton(R.string.dialog_select_camera,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startCamera();
                                    }
                                });
                builder.create().show();
            }
        });

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInitialState();
            }

        });


    }

    //region Camera Permissions
    //
    private void startCamera() {
        // Check for the external storage permission
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            PermissionUtils.requestPermissions((Activity)mContext);
        }
        else {
            // Launch the camera if the permission exists
            openCameraIntent();
        }
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null) {
           // Create a file to store the image
           File photoFile = null;
           try {
               photoFile = BitmapUtils.createImageFile(mContext);
           } catch (IOException e) {
               Timber.d("Error occurred while creating the file: " + e);
               return;
           }
           mImageUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".provider", photoFile);
           pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
           startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);

        }
    }
    //
    //endregion


    //region Gallery Permissions
    //
    private void startGalleryChooser() {
        if(ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE) !=  PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            PermissionUtils.requestPermissions((Activity)mContext);
        }
        else {
            openGalleryIntent();
        }

    }

    private void openGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_photo_dialog)),
                PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to use camera, read and write to external storage
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE: {

                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    // If you get permission, launch the camera
                    openCameraIntent();
                }
                else if (grantResults.length > 0 &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    // If you get permission, launch the gallery
                    openGalleryIntent();
                }
                else {

                    // If you do not get permission, show a Toast
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    // Get the bitmap and image path onActivityResult of an activity or fragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
            // don't compare the data to null, it will always come as  null
            // because we are providing a file URI, so load with the imageFilePath
            // we obtained before opening the cameraIntent
            if(mImageUri != null) {
                displayImage();
                Glide.with(this).load(mImageUri).into(mImage);
                processFileImage(mImageUri);

                // Uncomment this for testing ONLY:
                // processDummyData();
            }
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == PERMISSION_REQUEST_CODE) {
            mImageUri = data.getData();

            if(mImageUri != null) {
                displayImage();
                Glide.with(this).load(mImageUri).into(mImage);
                processFileImage(mImageUri);

                // Uncomment thjs for testing ony
                // processDummyData();
            }
        }
    }
    //
    //endregion


    //region Firebase Cloud Vision

    private void processFileImage(Uri imageUri) {
        FirebaseVisionImage image;
        try {
            image = FirebaseVisionImage.fromFilePath(this, imageUri);
            detectLandmark(image);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void detectLandmark(FirebaseVisionImage image) {

        FirebaseVisionCloudDetectorOptions options =
                new FirebaseVisionCloudDetectorOptions.Builder()
                        .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                        .setMaxResults(15)
                        .build();

        FirebaseVisionCloudLandmarkDetector detector = FirebaseVision.getInstance()
                .getVisionCloudLandmarkDetector(options);

        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionCloudLandmark>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionCloudLandmark> firebaseVisionCloudLandmarks) {
                        // Task completed successfully
                        // For now, I'm getting only one result. Later
                        if(firebaseVisionCloudLandmarks != null && firebaseVisionCloudLandmarks.size() > 0) {
                            setMyLandmark(firebaseVisionCloudLandmarks.get(0));
                            setViews(mMyLandmark);
                        }
                        else {
                            displayError();
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        Timber.d("Unable to detect image: " + e);
                        setViews(new MyLandmark());
                    }
                });

    }

    //endregion


    //region Setting Views
    // Use this method for test ONLY to avoid excessive calls to Google Cloud
    private void processDummyData() {

        mLoadingLayout.setVisibility(View.INVISIBLE);
        findViewById(R.id.ll_result_list_item).setVisibility(View.VISIBLE);


        String landmarkName = "Eiffel Tower";
        String location = "Paris, France";
        String confidence = "63.33%";
        String longitude = "48.8584";
        String latitude = "2.2945";

        if(mImageUri != null) {
            mLandmark.setText(landmarkName);
            mPlace.setText(location);
            mConfidence.setText(confidence);
        }

        mMyLandmark = new MyLandmark(landmarkName, confidence, location, longitude, latitude);

        mResultInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(com.brunogtavares.worldlandmarks.MainActivity.this, ImageInfoActivity.class);
                intent.putExtra(ImageInfoActivity.LANDMARK_BUNDLE_KEY, mMyLandmark);

                if(mImageUri != null) {
                    intent.putExtra(URI_KEY, mImageUri);
                    Timber.d("Path from Image uri: " + mImageUri.toString());
                }

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void setMyLandmark(FirebaseVisionCloudLandmark landmark) {

        String landmarkName = landmark.getLandmark();
        String location = LandmarkUtils.getLocation(this, landmark.getLocations());
        String confidence = LandmarkUtils.getPercentage(landmark.getConfidence());
        String longitude = String.valueOf(LandmarkUtils.getLongitude(landmark.getLocations()));
        String latitude = String.valueOf(LandmarkUtils.getLatitude(landmark.getLocations()));

        mMyLandmark = new MyLandmark(landmarkName, confidence, location, latitude, longitude);
    }


    private void setViews(MyLandmark myLandmark) {

        if(myLandmark == null) {
            displayError();
        }
        else {
            displayInfo();

            String landmarkName = myLandmark.getLandmarkName();
            String location = myLandmark.getLocation();
            String confidence = myLandmark.getConfidence();

            if(mImageUri != null) {
                mLandmark.setText(landmarkName);
                mPlace.setText(location);
                mConfidence.setText(confidence);
            }

        }

        mResultInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(com.brunogtavares.worldlandmarks.MainActivity.this, ImageInfoActivity.class);
                intent.putExtra(ImageInfoActivity.LANDMARK_BUNDLE_KEY, mMyLandmark);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                if(mImageUri != null) {
                    intent.putExtra(URI_KEY, mImageUri);
                }

                startActivity(intent);

            }
        });
    }
    //endregion


    //region Saving States

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mImageUri != null) {
            outState.putParcelable(URI_KEY, mImageUri);
        }
        if(mMyLandmark != null) {
            outState.putParcelable(RESULT_KEY, mMyLandmark);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mImageUri != null) {
            Glide.with(this).load(mImageUri).into(mImage);
            displayImage();
        }
        if(mMyLandmark != null) {
            setViews(mMyLandmark);
            // Uncomment process dummy data for testing ONLY
            // processDummyData();
            displayInfo();
        }

    }

    //endregion


    //region Displays

    private void displayImage() {
        mImage.setVisibility(View.VISIBLE);
        mGetImageButton.setVisibility(View.GONE);
        mClearButton.show();
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    private void displayInitialState() {

        mClearButton.hide();
        mImage.setVisibility(View.GONE);

        mGetImageButton.setVisibility(View.VISIBLE);

        mResultInfo.setVisibility(View.GONE);
        mNotFoundText.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);

        mImageUri = null;
        mLandmark.setText("");
        mPlace.setText("");
        mConfidence.setText("");
        mImage.setImageResource(0);

        mMyLandmark = null;

    }

    private void displayError() {
        mLoadingLayout.setVisibility(View.GONE);
        mResultInfo.setVisibility(View.GONE);
        mNotFoundText.setVisibility(View.VISIBLE);

    }

    private void displayInfo() {
        mLoadingLayout.setVisibility(View.GONE);
        mNotFoundText.setVisibility(View.GONE);
        mResultInfo.setVisibility(View.VISIBLE);
    }

    //endregion

    // Creates the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.action_get_landmark:
                // Do nothing
                return true;

            case R.id.action_my_landmarks:
                Intent goToLandmarksIntent = new Intent(this, MyLandmarksActivity.class);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(goToLandmarksIntent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);;
                } else {
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
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
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
            startActivity(registrationIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else {
            startActivity(registrationIntent);
        }

    }

}
