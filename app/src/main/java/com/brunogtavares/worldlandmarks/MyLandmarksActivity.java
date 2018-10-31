package com.brunogtavares.worldlandmarks;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brunogtavares.worldlandmarks.Firebase.FirebaseEntry;
import com.brunogtavares.worldlandmarks.model.MyLandmark;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyLandmarksActivity extends AppCompatActivity {

    @BindView(R.id.rv_mylandmark_list) RecyclerView mRecyclerView;

    FirebaseRecyclerAdapter mAdapter;
    MyLandmark mMyLandmark;

    String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_landmarks);

        ButterKnife.bind(this);

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setViews();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mAdapter.stopListening();
    }

    private void setViews() {

        Query query = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEntry.TABLE_USERS)
                .child(mUserId)
                .child(FirebaseEntry.TABLE_MYLANDMARKS);

        FirebaseRecyclerOptions<MyLandmark> options =
                new FirebaseRecyclerOptions.Builder<MyLandmark>()
                        .setQuery(query, MyLandmark.class)
                        .build();

       mAdapter = new FirebaseRecyclerAdapter<MyLandmark, MyLandmarkHolder>(options) {

           @NonNull
           @Override
           public MyLandmarkHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

               View view = LayoutInflater.from(viewGroup.getContext())
                       .inflate(R.layout.mylandmark_list_item, viewGroup, false);

               return new MyLandmarkHolder(view);
           }

           @Override
           protected void onBindViewHolder(@NonNull MyLandmarkHolder holder, int position, @NonNull MyLandmark model) {
               mMyLandmark = model;

               Glide.with(MyLandmarksActivity.this).load(mMyLandmark.getImageUri()).into(holder.mThumbnailView);
               holder.mMyLandmarkName.setText(mMyLandmark.getLandmark());
               holder.mMyLandmarkLocation.setText(mMyLandmark.getLocation());
           }
       };

        mRecyclerView.setAdapter(mAdapter);
    }


    public class MyLandmarkHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_mylandmark_thumbnail)ImageView mThumbnailView;
        @BindView(R.id.tv_mylandmark_name) TextView mMyLandmarkName;
        @BindView(R.id.tv_mylandmark_location) TextView mMyLandmarkLocation;

        public MyLandmarkHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
