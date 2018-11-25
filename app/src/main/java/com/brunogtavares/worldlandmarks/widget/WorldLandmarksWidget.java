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


    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Construct the Remoteviews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.world_landmarks_widget);

        // RemoteViews Service needed to provide adapter for ListView
        Intent serviceIntent = new Intent(context, WorldLandmarksWidgetService.class);

        // Passing app widget id to that Remote Service
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        // Setting a unique Uri to the intent
        serviceIntent.setData(Uri.parse(
                serviceIntent.toUri(Intent.URI_INTENT_SCHEME)
        ));

        // Setting the title clickable
        Intent openAppIntent = new Intent(context, MyLandmarksActivity.class);
        PendingIntent myLandmarksPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent,
                0);
        views.setOnClickPendingIntent(R.id.tv_widget_title, myLandmarksPendingIntent);

        // Setting adapter to listView of the widget
        views.setRemoteAdapter(R.id.lv_widget_listview, serviceIntent);

        // Setting an empty view in case of no data
        views.setTextViewText(R.id.tv_widget_empty_view, context.getString(R.string.no_landmarks));
        views.setEmptyView(R.id.lv_widget_listview, R.id.tv_widget_empty_view);

        views.setTextViewText(R.id.tv_widget_title, context.getString(R.string.app_name));

        // Setup PendingIntent for the ListView
        Intent detailActivityIntent = new Intent(context, MyLandmarkDetailActivity.class);
        PendingIntent detailActivityPendingIntent = PendingIntent.getActivity(context, 0, detailActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.lv_widget_listview, detailActivityPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName widget = new ComponentName(context.getPackageName(),
                WorldLandmarksWidget.class.getName());
        int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(widget);
        onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);

        super.onReceive(context, intent);
    }
}

