package com.brunogtavares.worldlandmarks.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.brunogtavares.worldlandmarks.R;

/**
 * Implementation of App Widget functionality.
 */
public class WorldLandmarksWidget extends AppWidgetProvider {

    public static String ACTION_CLICK_URL = "ACTION_CLICK_URL";
    public static String EXTRA_ITEM_URL = "EXTRA_ITEM_URL";

//    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
//                                int appWidgetId) {
//
//        CharSequence widgetText = context.getString(R.string.appwidget_text);
//        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.world_landmarks_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
//
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
//    }

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
            // updateAppWidget(context, appWidgetManager, appWidgetId);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.world_landmarks_widget);

            // Setting the ListView intent and adapter
            Intent intent = new Intent(context, WorldLandmarksWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            remoteViews.setEmptyView(R.id.lv_widget_listview, R.id.tv_widget_empty_view);
            remoteViews.setRemoteAdapter(R.id.lv_widget_listview, intent);

            // Setting the refresh button intent
//            Intent refreshIntent = new Intent(context, WorldLandmarksWidget.class);
//            refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//            refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//            PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,
//                    refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            remoteViews.setOnClickPendingIntent(// INSERT BUTTON LAYOUT,
//                    refreshPendingIntent);

            // Setting the click intent on the list items
            Intent clickIntent = new Intent(context, WorldLandmarksWidget.class);
            clickIntent.setAction(ACTION_CLICK_URL);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.lv_widget_listview, clickPendingIntent);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_widget_listview);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_CLICK_URL)) {
            String url = intent.getStringExtra(EXTRA_ITEM_URL);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            update(context);
        }
        super.onReceive(context, intent);
    }

}

