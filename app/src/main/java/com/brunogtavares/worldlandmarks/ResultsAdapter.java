package com.brunogtavares.worldlandmarks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brunogtavares.worldlandmarks.utils.LandmarkUtils;
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by brunogtavares on 10/16/18.
 */

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultViewHolder> {

    private List<FirebaseVisionCloudLandmark> mFirebaseVisionCloudLandmarks;
    private Context mContext;
    private final ResultsAdapterOnClickHandler mClickHandler;

    public interface ResultsAdapterOnClickHandler {
        void onClick(FirebaseVisionCloudLandmark landmark);
    }

    public ResultsAdapter(ResultsAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.result_list_item, viewGroup, false);

        return new ResultViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder resultViewHolder, int position) {
        FirebaseVisionCloudLandmark landmark = mFirebaseVisionCloudLandmarks.get(position);

        String landmarkName = landmark.getLandmark();
        String confidenceValue = LandmarkUtils.getPercentage(landmark.getConfidence());
        String location = mContext != null ?
                LandmarkUtils.getLocation(mContext, landmark.getLocations()) : "";


        resultViewHolder.mLandmarkName.setText(landmarkName);
        resultViewHolder.mConfidenceValue.setText(confidenceValue);
        resultViewHolder.mLocation.setText(location);

    }

    @Override
    public int getItemCount() {
        return mFirebaseVisionCloudLandmarks == null? 0 : mFirebaseVisionCloudLandmarks.size();
    }

    public List<FirebaseVisionCloudLandmark> getFirebaseVisionCloudLandmarks() {
        return mFirebaseVisionCloudLandmarks;
    }

    public void setFirebaseVisionCloudLandmarks(List<FirebaseVisionCloudLandmark> firebaseVisionCloudLandmarks) {
        this.mFirebaseVisionCloudLandmarks = firebaseVisionCloudLandmarks;
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.mContext = context;
    }


    public class ResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_landmark_name) TextView mLandmarkName;
        @BindView(R.id.tv_location) TextView mLocation;
        @BindView(R.id.tv_confidence_value) TextView mConfidenceValue;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onClick(mFirebaseVisionCloudLandmarks.get(position));
        }
    }
}
