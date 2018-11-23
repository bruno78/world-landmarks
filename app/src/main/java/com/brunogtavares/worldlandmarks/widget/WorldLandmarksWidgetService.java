package com.brunogtavares.worldlandmarks.widget;

import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * Created by brunogtavares on 11/23/18.
 */

public class WorldLandmarksWidgetService extends RemoteViewsService {

    public WorldLandmarksWidgetService(){}

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WorldLandmarksViewsFactory(this.getApplicationContext(), intent);
    }
}
