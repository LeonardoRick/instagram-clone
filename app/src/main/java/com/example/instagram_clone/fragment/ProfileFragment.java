package com.example.instagram_clone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.activity.EditProfileActivity;

public class ProfileFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView numberPosts, numberFollowers, numberFollowing;
    private CircleImageView profileImage;
    private Button buttonEditProfile;
    private GridView gridView;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViewElements(view);

        return view;
    }

    public void initViewElements(View view) {
        progressBar = view.findViewById(R.id.progressBarProfile);
        profileImage = view.findViewById(R.id.circleImageViewProfile);

        numberPosts = view.findViewById(R.id.numberPosts);
        numberFollowers = view.findViewById(R.id.numberFollowers);
        numberFollowing = view.findViewById(R.id.numberFollowing);

        buttonEditProfile = view.findViewById(R.id.profileActionButton);
        gridView = view.findViewById(R.id.gridView);

        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });
    }

}