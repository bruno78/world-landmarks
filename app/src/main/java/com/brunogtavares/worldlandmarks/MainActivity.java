package com.brunogtavares.worldlandmarks;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmarkDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionLatLng;
import com.mindorks.paracamera.Camera;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * https://firebase.google.com/docs/ml-kit/android/recognize-landmarks
 */
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private ImageView mImage;
    private TextView mResult;

    Camera mCamera;

    FirebaseVisionCloudDetectorOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Planting Timber
        if(BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());

        mImage = (ImageView) findViewById(R.id.iv_image);
        mResult = (TextView) findViewById(R.id.tv_result);

        Button button = (Button) findViewById(R.id.bt_camera);

        mOptions =
                new FirebaseVisionCloudDetectorOptions.Builder()
                        .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                        .setMaxResults(15)
                        .build();

        mCamera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)
                .setDirectory("WorldLandmakrs")
                .setName("landmark_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                // .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                                        takePicture();
                                    }
                                });
                builder.create().show();
            }
        });
    }

    private void takePicture() {

        // Check for the external storage permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            requestPermissions();
        }
        else {
            // Launch the camera if the permission exists
            launchCamera();
        }
    }

    private void startGalleryChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                PERMISSION_REQUEST_CODE);
    }

    private void requestPermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            ActivityCompat.requestPermissions( MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);

        }
        else {
            ActivityCompat.requestPermissions( MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE: {

                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // If you get permission, launch the camera
                    launchCamera();
                }
                else if (grantResults.length > 0 &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    // If you get permission, launch the gallery
                    startGalleryChooser();
                }
                else {

                    // If you do not get permission, show a Toast
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    private void launchCamera() {

        try {
            mCamera.takePicture();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get the bitmap and image path onActivityResult of an activity or fragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == Camera.REQUEST_TAKE_PHOTO){
            Bitmap bitmap = mCamera.getCameraBitmap();
            if(bitmap != null) {
                Glide.with(this).load(bitmap).into(mImage);
                // mImage.setImageBitmap(bitmap);
                processBitmapImage(bitmap);
            }else{
                Toast.makeText(this.getApplicationContext(), R.string.photo_not_taken, Toast.LENGTH_SHORT).show();
            }
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == PERMISSION_REQUEST_CODE) {
            final Uri imageUri = data.getData();
            if(imageUri != null) {
                Glide.with(this).load(imageUri).into(mImage);
                // mImage.setImageURI(imageUri);
                processFileImage(imageUri);
            }
        }
    }


    private void processBitmapImage(Bitmap bitmapImage) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmapImage);
        detectLandmark(image);
    }

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
        FirebaseVisionCloudLandmarkDetector detector = FirebaseVision.getInstance()
                .getVisionCloudLandmarkDetector(mOptions);
        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionCloudLandmark>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionCloudLandmark> firebaseVisionCloudLandmarks) {
                        // Task completed successfully
                        loadViews(firebaseVisionCloudLandmarks);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        mResult.setText(R.string.landmark_not_detected);
                    }
                });

    }

    private void loadViews(List<FirebaseVisionCloudLandmark> firebaseVisionCloudLandmarks) {
        String message = "";


        if (firebaseVisionCloudLandmarks != null) {
            if(firebaseVisionCloudLandmarks.size() > 0) {

                for (FirebaseVisionCloudLandmark landmark: firebaseVisionCloudLandmarks) {

                    String landmarkName = landmark.getLandmark();
                    String entityId = landmark.getEntityId();
                    float confidence = landmark.getConfidence();

                    // Multiple locations are possible, e.g., the location of the depicted
                    // landmark and the location the picture was taken.
                    for (FirebaseVisionLatLng loc: landmark.getLocations()) {
                        double latitude = loc.getLatitude();
                        double longitude = loc.getLongitude();
                    }

                    message = message + "    " + landmarkName + " " + confidence;
                    message += "\n";
                }
            }
        }
        else {
            message = "Nothing found";
        }

        mResult.setText(message);
    }

    private String getCountryName(double latitude, double longitude) {
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
            return addresses.get(0).getCountryName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // The bitmap is saved in the app's folder
    //  If the saved bitmap is not required use following code
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera.deleteImage();
    }

    // TODO create a recycler View
    // TODO save instance state
    // TODO Fix views

}
