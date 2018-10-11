package com.brunogtavares.worldlandmarks;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmarkDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionLatLng;
import com.mindorks.paracamera.Camera;

import java.util.List;

/**
 * https://firebase.google.com/docs/ml-kit/android/recognize-landmarks
 */
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private ImageView mImage;
    private TextView mREsult;

    Camera mCamera;

    FirebaseVisionCloudDetectorOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImage = (ImageView) findViewById(R.id.iv_image);
        mREsult = (TextView) findViewById(R.id.tv_result);

        Button button = (Button) findViewById(R.id.bt_camera);

        mOptions =
                new FirebaseVisionCloudDetectorOptions.Builder()
                        .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                        .setMaxResults(15)
                        .build();

        mCamera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)
                .setDirectory("landmark_pics")
                .setName("landmark_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                // .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
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

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ActivityCompat.requestPermissions( MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);

        }
        else {
            ActivityCompat.requestPermissions( MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
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
                else {

                    // If you do not get permission, show a Toast
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
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
                mImage.setImageBitmap(bitmap);
                processImage(bitmap);
            }else{
                Toast.makeText(this.getApplicationContext(),"Picture not taken!",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void processImage(Bitmap bitmapImage) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmapImage);
        FirebaseVisionCloudLandmarkDetector detector = FirebaseVision.getInstance()
                .getVisionCloudLandmarkDetector(mOptions);

        Task<List<FirebaseVisionCloudLandmark>> result = detector.detectInImage(image)
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
                        mREsult.setText("Could not detect the landmark... Try again!");
                    }
                });

    }

    private void loadViews(List<FirebaseVisionCloudLandmark> firebaseVisionCloudLandmarks) {
        String message = "";

        if (firebaseVisionCloudLandmarks != null) {

            for (FirebaseVisionCloudLandmark landmark: firebaseVisionCloudLandmarks) {

                Rect bounds = landmark.getBoundingBox();
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
        else {
            message = "Nothing found";
        }

        mREsult.setText(message);
    }

    // The bitmap is saved in the app's folder
    //  If the saved bitmap is not required use following code
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera.deleteImage();
    }


}
