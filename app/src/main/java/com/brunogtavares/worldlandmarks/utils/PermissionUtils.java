package com.brunogtavares.worldlandmarks.utils;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;


/**
 * Created by brunogtavares on 10/20/18.
 */

public class PermissionUtils {

    public static final int PERMISSION_REQUEST_CODE = 1;

    public static void requestPermissions(Activity activity) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

            ActivityCompat.requestPermissions( activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    PermissionUtils.PERMISSION_REQUEST_CODE);

        }
        else {

            ActivityCompat.requestPermissions( activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    PermissionUtils.PERMISSION_REQUEST_CODE);

        }
    }


}
