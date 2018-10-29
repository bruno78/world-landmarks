package com.brunogtavares.worldlandmarks;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brunogtavares.worldlandmarks.model.MyLandmark;

import java.util.List;

/**
 * Created by brunogtavares on 10/28/18.
 */

public class MyLandmarkAdapter extends RecyclerView.Adapter<MyLandmarkAdapter.MyLandmarkViewHolder> {

    private List<MyLandmark> mMyLandmarkList;
    private Context mContext;
    private final MyLandmarkAdapterOnClickHandler mClickHandler;

    public interface MyLandmarkAdapterOnClickHandler {
        void onClick(MyLandmark myLandmark);
    }

    public MyLandmarkAdapter(MyLandmarkAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MyLandmarkViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.result_list_item, viewGroup, false);

        return new MyLandmarkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyLandmarkViewHolder myLandmarkViewHolder, int position) {
        MyLandmark myLandmark mMyLandmarkList.get(position);


    }

    @Override
    public int getItemCount() {
        return mMyLandmarkList != null ? mMyLandmarkList.size() : 0;
    }

    public class MyLandmarkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public MyLandmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO finish implementing the views...
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onClick(mMyLandmarkList.get(position));
        }
    }
}
