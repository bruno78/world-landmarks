package com.brunogtavares.worldlandmarks;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brunogtavares.worldlandmarks.model.MyLandmark;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by brunogtavares on 11/24/18.
 */

public class MyLandmarksAdapter extends RecyclerView.Adapter<MyLandmarksAdapter.MyLandmarksViewHolder> {

    private List<MyLandmark> mMyLandmarks;
    private MyLandmarksAdapterOnClickHandler mClickHandler;
    private Context mContext;

    public interface MyLandmarksAdapterOnClickHandler {
        void onClick(MyLandmark myLandmark);
    }

    public MyLandmarksAdapter(MyLandmarksAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MyLandmarksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.mylandmark_list_item, viewGroup, false);

        return new MyLandmarksViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyLandmarksViewHolder holder, int position) {

        MyLandmark myLandmark = mMyLandmarks.get(position);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(userId);
        storageReference.child(myLandmark.getImageUri()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext).load(uri)
                        .into(holder.mThumbnailView);
            }
        });

        holder.mMyLandmarkName.setText(myLandmark.getLandmarkName());
        holder.mMyLandmarkLocation.setText(myLandmark.getLocation());
    }

    @Override
    public int getItemCount() {
        return mMyLandmarks == null ? 0 : mMyLandmarks.size();
    }

    public void setMyLandmarks(List<MyLandmark> myLandmarks) {
        mMyLandmarks = myLandmarks;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public class MyLandmarksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_mylandmark_thumbnail)ImageView mThumbnailView;
        @BindView(R.id.tv_mylandmark_name) TextView mMyLandmarkName;
        @BindView(R.id.tv_mylandmark_location) TextView mMyLandmarkLocation;

        public MyLandmarksViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            MyLandmark myLandmark = mMyLandmarks.get(position);
            mClickHandler.onClick(myLandmark);
        }
    }
}
