package com.brunogtavares.worldlandmarks.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.brunogtavares.worldlandmarks.MainActivity;
import com.brunogtavares.worldlandmarks.MyLandmarkDetailActivity;
import com.brunogtavares.worldlandmarks.MyLandmarksActivity;
import com.brunogtavares.worldlandmarks.R;

/**
 * Implementation of App Widget functionality.
 */
public class WorldLandmarksWidget extends AppWidgetProvider {

    public static String ACTION_CLICK = "ACTION_CLICK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        update(context, appWidgetManager, appWidgetIds);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public void update(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds =
                appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
        update(context, appWidgetManager, appWidgetIds);
    }

    public void update(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.world_landmarks_widget);

            // Create an Intent to launch MainActivity when clicked on title
            Intent titleIntent = new Intent(context, MyLandmarksActivity.class);
            PendingIntent titlePendingIntent = PendingIntent.getActivity(context, 0, titleIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.tv_widget_title, titlePendingIntent);


            // Setting the ListView intent and adapter
            Intent intent = new Intent(context, WorldLandmarksWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            remoteViews.setRemoteAdapter(R.id.lv_widget_listview, intent);
            remoteViews.setEmptyView(R.id.lv_widget_listview, R.id.tv_widget_empty_view);

            // Setting the refresh feature on empty text view.
            Intent refreshIntent = new Intent(context, WorldLandmarksWidget.class);
            refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,
                    refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.tv_widget_empty_view,
                    refreshPendingIntent);

            // Setting the click intent on the list items
            Intent clickIntent = new Intent(context, WorldLandmarksWidget.class);
            clickIntent.setAction(ACTION_CLICK);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.lv_widget_listview, clickPendingIntent);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_widget_listview);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            update(context);
        }
        super.onReceive(context, intent);
    }
}

