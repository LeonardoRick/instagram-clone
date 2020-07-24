package com.example.instagram_clone.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram_clone.R;
import com.example.instagram_clone.adapter.FeedAdapter;
import com.example.instagram_clone.model.FeedItem;
import com.example.instagram_clone.model.post.Post;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.model.user.UserHelper;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<FeedItem> feedList = new ArrayList<>();
    private FeedAdapter feedAdapter;
    private User loggedUser;

    private DatabaseReference feedRef;
    private ValueEventListener valueEventListener;
    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        recoverFeedInfo();
    }

    @Override
    public void onStop() {
        super.onStop();
        feedRef.removeEventListener(valueEventListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_feed, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFeed);
        loggedUser = UserHelper.getLogged();
        configRecyclerView();
        return view;
    }

    private void configRecyclerView() {
        recyclerView.setHasFixedSize(true);
        // LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Adapter
        feedAdapter = new FeedAdapter(feedList);
        recyclerView.setAdapter(feedAdapter);
    }


    /**
     * Used to recover the list of posts that are saved on logged user
     * feed node on firebase, so he can see all posts from people who he is following
     */
    private void recoverFeedInfo() {

        feedRef = FirebaseConfig.getFirebaseDatabase()
                .child(Constants.FeedNode.KEY)
                .child(loggedUser.getId());

        valueEventListener =
                feedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Post post = postSnapshot.getValue(Post.class);
                            recoverUserWhoPostedInfo(post);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    /**
     * Recover user who posted a specifc post, to show his name and
     * picture above his post on feed;
     * @param post of user
     */
    private void recoverUserWhoPostedInfo(final Post post) {
        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.UsersNode.KEY)
                .child(post.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FeedItem feedItem = new FeedItem();
                        User userWhoPosted = dataSnapshot.getValue(User.class);
                        feedItem.setPost(post);
                        feedItem.setUserWhoPosted(userWhoPosted);
                        feedList.add(feedItem);
                        feedAdapter.notifyDataSetChanged();
                        Collections.sort(feedList);
                        Collections.reverse(feedList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}