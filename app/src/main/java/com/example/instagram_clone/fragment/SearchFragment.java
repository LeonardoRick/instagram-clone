package com.example.instagram_clone.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.example.instagram_clone.R;
import com.example.instagram_clone.activity.FriendProfileActivity;
import com.example.instagram_clone.adapter.UserAdapter;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.model.user.UserHelper;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.example.instagram_clone.utils.RecyclerItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;

    private List<User> usersList = new ArrayList<>();
    private UserAdapter adapter;

    public SearchFragment() {
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initViewElements(view);
        return view;
    }

    private void initViewElements(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewSearch);
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        configRecyclerView();
        configSearchView();
    }


    /**
     * Config recycler view that will show users from search
     */
    private void configRecyclerView() {
        recyclerView.setHasFixedSize(true);
        // Layout Manager
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayout);
        // Adatper
        adapter = new UserAdapter(usersList);
        recyclerView.setAdapter(adapter);
        setRecyclerViewListener();
    }

    /**
     * Config search view listener to search users
     */
    private void configSearchView(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUsers(newText);
                return true;
            }
        });
    }

    /**
     * Method that search users since user typed 2 chars or more
     * @param text to search user name
     */
    private void searchUsers(String text) {
        if (text.length() > 1) {
            Query query = FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.UsersNode.KEY)
                    .orderByChild(Constants.UsersNode.NAME)
                    .startAt(text)
                    .endAt(text + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    usersList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            usersList.add(ds.getValue(User.class));
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * start FriendProfileActivity with info form selected user
     */
    private void setRecyclerViewListener() {
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getContext(), FriendProfileActivity.class);
                                intent.putExtra(Constants.Intent.SELECTED_USER, usersList.get(position));
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) { }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
                        }
                )
        );
    }
}