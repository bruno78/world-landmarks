package com.brunogtavares.worldlandmarks;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.brunogtavares.worldlandmarks.Firebase.FirebaseEntry;
import com.brunogtavares.worldlandmarks.model.MyLandmark;
import com.brunogtavares.worldlandmarks.utils.NetworkUtils;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.brunogtavares.worldlandmarks.MyLandmarkDetailActivity.MY_LANDMARK_KEY;

public class MyLandmarksActivity extends AppCompatActivity implements
        MyLandmarksAdapter.MyLandmarksAdapterOnClickHandler {

    @BindView(R.id.rv_mylandmark_list) RecyclerView mRecyclerView;
    @BindView(R.id.ll_mylandmarks_loading) LinearLayout mLoadingLayout;
    @BindView(R.id.tv_mylandmarks_no_connection) TextView mNoConnectionTextView;
    @BindView(R.id.prl_refresh_layout) PullRefreshLayout mPullRefreshLayout;

    private MyLandmarksAdapter mAdapter;
    private String mUserId;
    private Query mQuery;
    private ValueEventListener mMyLandmarkListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_landmarks);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null) goToRegistrationActivity();

        ButterKnife.bind(this);

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new MyLandmarksAdapter(this);
        mAdapter.setContext(getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        mPullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setViews();
            }
        });

        // Check for network connection
        boolean isConnected = NetworkUtils.checkForNetworkStatus(this);
        if(isConnected) {
            mNoConnectionTextView.setVisibility(View.GONE);
            setViews();
        }
        else {
            Toast.makeText(this, R.string.check_connection, Toast.LENGTH_SHORT).show();
            mLoadingLayout.setVisibility(View.GONE);
            mNoConnectionTextView.setVisibility(View.VISIBLE);
            mPullRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mQuery != null)
            mQuery.removeEventListener(mMyLandmarkListener);
    }

    private void setViews() {

        mQuery = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEntry.TABLE_USERS)
                .child(mUserId)
                .child(FirebaseEntry.TABLE_MYLANDMARKS);

        if (mUserId != null) {
            mMyLandmarkListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<MyLandmark> landmarkList = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        landmarkList.add(child.getValue(MyLandmark.class));
                    }
                    mAdapter.setMyLandmarks(landmarkList);
                    mAdapter.notifyDataSetChanged();
                    mLoadingLayout.setVisibility(View.GONE);
                    mPullRefreshLayout.setRefreshing(false);
                    mNoConnectionTextView.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mQuery.addValueEventListener(mMyLandmarkListener);
        }
    }

    @Override
    public void onClick(MyLandmark myLandmark) {
        Intent intent = new Intent(getApplicationContext(), MyLandmarkDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MY_LANDMARK_KEY, myLandmark);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    // Creates the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_get_landmark:
                Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
                startActivity(goToMainActivityIntent);
                return true;
            case R.id.action_my_landmarks:
                // Do nothing
                return true;
            case R.id.action_logout:
                signOut();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_logout)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        goToRegistrationActivity();
                    }})
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    private void goToRegistrationActivity() {
        Intent registrationIntent = new Intent(this, EmailPasswordActivity.class);
        startActivity(registrationIntent);
        finish();
    }
}
