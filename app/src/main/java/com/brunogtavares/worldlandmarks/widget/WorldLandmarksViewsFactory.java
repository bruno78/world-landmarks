package com.brunogtavares.worldlandmarks.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.brunogtavares.worldlandmarks.Firebase.FirebaseEntry;
import com.brunogtavares.worldlandmarks.MyLandmarkDetailActivity;
import com.brunogtavares.worldlandmarks.R;
import com.brunogtavares.worldlandmarks.model.MyLandmark;
import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.brunogtavares.worldlandmarks.MyLandmarkDetailActivity.MY_LANDMARK_KEY;

/**
 * Created by brunogtavares on 11/23/18.
 */

public class WorldLandmarksViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int mAppWidgetId;
    private List<MyLandmark> mMyLandmarkList;
    private StorageReference mStorageReference;
    private ValueEventListener mWorldLandmarkListener;
    private Query mQuery;
    private String mUserId;

    public WorldLandmarksViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null) {
            mMyLandmarkList = new ArrayList<>();
            mUserId = firebaseAuth.getCurrentUser().getUid();
            mStorageReference = FirebaseStorage.getInstance().getReference().child(mUserId);

            getList();
        }

    }

    private void getList() {

        // Getting the query
        mQuery = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEntry.TABLE_USERS)
                .child(mUserId)
                .child(FirebaseEntry.TABLE_MYLANDMARKS)
                .limitToLast(3);

        // Setting up the listener
        mWorldLandmarkListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMyLandmarkList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    mMyLandmarkList.add(child.getValue(MyLandmark.class));
                }
                AppWidgetManager.getInstance(mContext)
                        .notifyAppWidgetViewDataChanged(mAppWidgetId, R.id.lv_widget_listview);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mQuery.addValueEventListener(mWorldLandmarkListener);
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {
        if (mQuery != null)
            mQuery.removeEventListener(mWorldLandmarkListener);
    }

    @Override
    public int getCount() {
        return mMyLandmarkList == null? 0 : mMyLandmarkList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        final RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_list_item);

        MyLandmark myLandmark = mMyLandmarkList.get(position);

        StorageReference imagePath = mStorageReference.child(myLandmark.getImageUri());

        try {
            Bitmap bitmap = GlideApp.with(mContext)
                    .asBitmap()
                    .load(imagePath)
                    .submit(100, 80)
                    .get();
            remoteViews.setImageViewBitmap(R.id.iv_widget_landmark_image, bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(mContext, MyLandmarkDetailActivity.class);
        intent.putExtra(MY_LANDMARK_KEY, myLandmark);
        remoteViews.setOnClickFillInIntent(R.id.ll_widget_list_item, intent);

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

