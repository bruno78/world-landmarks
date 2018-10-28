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
