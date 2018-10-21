package com.brunogtavares.worldlandmarks.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.brunogtavares.worldlandmarks.MainActivity;
import com.mindorks.paracamera.Camera;

/**
 * Created by brunogtavares on 10/20/18.
 */

public class CameraUtils {

    public static final int PERMISSION_REQUEST_CODE = 1;

    public static Camera buildCamera(Activity activity) {

        return new Camera.Builder()
                .resetToCorrectOrientation(true) // it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)
                .setDirectory("WorldLandmakrs")
                .setName("landmark_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .build(activity);
    }

    public static void takePicture(Context context, Camera camera) {

        // Check for the external storage permission
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            requestPermissions((Activity) context);
        }
        else {
            // Launch the camera if the permission exists
            launchCamera(camera);
        }
    }

    public static void launchCamera(Camera camera) {

        try {
            camera.takePicture();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void requestPermissions(Activity activity) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

            ActivityCompat.requestPermissions( activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    CameraUtils.PERMISSION_REQUEST_CODE);

        }
        else {

            ActivityCompat.requestPermissions( activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    CameraUtils.PERMISSION_REQUEST_CODE);

        }
    }


}
