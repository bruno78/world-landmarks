package com.brunogtavares.worldlandmarks;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.brunogtavares.worldlandmarks.MyLandmarkDetailActivity.MY_LANDMARK_KEY;

public class MyLandmarksActivity extends AppCompatActivity {

    @BindView(R.id.rv_mylandmark_list) RecyclerView mRecyclerView;

    // TODO Test the recycler view.
    // Make sure each item is clickable

    FirebaseRecyclerAdapter mAdapter;
    MyLandmark mMyLandmark;

    String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_landmarks);

        ButterKnife.bind(this);

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

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
           protected void onBindViewHolder(@NonNull final MyLandmarkHolder holder, int position, @NonNull MyLandmark model) {
               mMyLandmark = model;

               String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
               StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(userId);
               storageReference.child(mMyLandmark.getImageUri()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                       Glide.with(MyLandmarksActivity.this).load(uri)
                               .into(holder.mThumbnailView);
                   }
               });

               holder.mMyLandmarkName.setText(mMyLandmark.getLandmarkName());
               holder.mMyLandmarkLocation.setText(mMyLandmark.getLocation());
           }
       };
        findViewById(R.id.pb_loading_list).setVisibility(View.GONE);
        findViewById(R.id.tv_mylandmarks_loading).setVisibility(View.GONE);

        mRecyclerView.setAdapter(mAdapter);
    }


    public class MyLandmarkHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_mylandmark_thumbnail)ImageView mThumbnailView;
        @BindView(R.id.tv_mylandmark_name) TextView mMyLandmarkName;
        @BindView(R.id.tv_mylandmark_location) TextView mMyLandmarkLocation;

        public MyLandmarkHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), MyLandmarkDetailActivity.class);

            Bundle bundle = new Bundle();
            bundle.putParcelable(MY_LANDMARK_KEY, mMyLandmark);
            intent.putExtras(bundle);
            startActivity(intent);

        }
    }
}
