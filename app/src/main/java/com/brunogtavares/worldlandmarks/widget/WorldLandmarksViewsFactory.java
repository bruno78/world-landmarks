package com.brunogtavares.worldlandmarks.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.brunogtavares.worldlandmarks.Firebase.FirebaseEntry;
import com.brunogtavares.worldlandmarks.R;
import com.brunogtavares.worldlandmarks.model.MyLandmark;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by brunogtavares on 11/23/18.
 */

public class WorldLandmarksViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int mAppWidgetId;
    private List<MyLandmark> mMyLandmarkList;
    private String mUserId;

    public WorldLandmarksViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        mMyLandmarkList = new ArrayList<>();
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getList();
    }

    private void getList() {
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEntry.TABLE_USERS)
                .child(mUserId)
                .child(FirebaseEntry.TABLE_MYLANDMARKS)
                .limitToLast(3)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            mMyLandmarkList.add(0, child.getValue(MyLandmark.class));
                        }

                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
                        int appWigetIds[] = appWidgetManager
                                .getAppWidgetIds(new ComponentName(mContext, WorldLandmarksWidget.class));
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWigetIds, R.id.lv_widget_listview);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mMyLandmarkList == null? 0 : mMyLandmarkList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        MyLandmark myLandmark = mMyLandmarkList.get(position);

        final RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_list_item);

        try {
            Bitmap bitmap = Glide.with(mContext)
                    .asBitmap()
                    .load(myLandmark.getImageUri())
                    .submit(100,80)
                    .get();
            remoteViews.setImageViewBitmap(R.id.iv_widget_landmark_image, bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        remoteViews.setTextViewText(R.id.tv_widget_landmark_name, myLandmark.getLandmarkName());
        remoteViews.setTextViewText(R.id.tv_widget_landmark_location, myLandmark.getLocation());

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
