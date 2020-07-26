package com.example.instagram_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.adapter.ThumbnailAdapter;
import com.example.instagram_clone.model.post.Post;
import com.example.instagram_clone.model.post.PostHelper;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.model.user.UserHelper;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.BitmapHelper;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.example.instagram_clone.utils.MessageHelper;
import com.example.instagram_clone.utils.RecyclerItemClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    static { System.loadLibrary("NativeImageProcessor"); }

    private Bitmap image;
    private Bitmap filteredImage;
    private List<ThumbnailItem> thumbnailsList = new ArrayList<>();
    private User loggedUser;
    private RecyclerView recyclerFilters;
    private ThumbnailAdapter adapter;

    private ImageView selectedImageView;
    private EditText imageDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black); // change backbutton icon

        loggedUser = UserHelper.getLogged();
        initViewElements();
    }

    private void initViewElements() {
        selectedImageView = findViewById(R.id.selectedImageView);
        imageDesc = findViewById(R.id.textFilterDescription);

        configRecyclerView();
        recoverSelectedImageInfo();
        recoverLoggedUserCompleteInfo();

    }

    private void recoverSelectedImageInfo() {
        if (getIntent().getExtras() != null) {
            String imagePath = (String) getIntent().getExtras().getSerializable(Constants.IntentKey.SELECTED_IMAGE);
            Uri selectedImageUri  = Uri.parse(imagePath);

            image = BitmapHelper.getBitmap(this, selectedImageUri);
            image = image.copy(Bitmap.Config.ARGB_8888, true);
            filteredImage = image.copy(Bitmap.Config.ARGB_8888, true);
            selectedImageView.setImageBitmap(image);

            recoverFiltersList();
        }
    }

    /**
     * Method used to recover complete info of user.
     * UserHelper.getLogged() returns only Firebase Auth info of user
     * (like image, name, email and password). This method is used to
     * recover the rest of info we saved about the user on the databse
     */
    private void recoverLoggedUserCompleteInfo() {
        MessageHelper.openLoadingDialog(this, "Carregando informações, aguarde");
        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.UsersNode.KEY)
                .child(loggedUser.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // used only to update user number of posts on database
                        loggedUser = dataSnapshot.getValue(User.class);
                        MessageHelper.closeLoadingDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        MessageHelper.closeLoadingDialog();
                        MessageHelper.showLongToast("Erro ao carregar informações do usuário, não será possível publicar agora");
                    }
                });
    }

    /**
     * Method called to create list o filters that will be showed
     * for user to select filter of his picture
     */
    private void recoverFiltersList() {
        ThumbnailsManager.clearThumbs();
        thumbnailsList.clear();

        // Config default template image filter
        ThumbnailItem noFilterItem = new ThumbnailItem();
        noFilterItem.image = image;
        noFilterItem.filterName = "Sem Filtro";
        ThumbnailsManager.addThumb(noFilterItem);

        // List all library filters
        List<Filter> filters = FilterPack.getFilterPack(this);
        for (Filter filter : filters) {
            ThumbnailItem item = new ThumbnailItem();
            item.image = image;
            item.filterName = filter.getName();
            item.filter = filter;

            ThumbnailsManager.addThumb(item);
        }

        thumbnailsList.addAll(ThumbnailsManager.processThumbs(this));
        adapter.notifyDataSetChanged();
    }


    private void configRecyclerView() {
        recyclerFilters = findViewById(R.id.recyclerFilters);
        recyclerFilters.setHasFixedSize(true);
        // Layout Manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerFilters.setLayoutManager(layoutManager);
        // Adapter
        adapter = new ThumbnailAdapter(thumbnailsList);
        recyclerFilters.setAdapter(adapter);
        setRecyclerViewListener();
    }

    private void setRecyclerViewListener() {
        recyclerFilters.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerFilters,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                filteredImage = image.copy(Bitmap.Config.ARGB_8888, true);
                                Filter filter = thumbnailsList.get(position).filter;
                                selectedImageView.setImageBitmap(filter.processFilter(filteredImage));
                            }

                            @Override
                            public void onLongItemClick(View view, int position) { }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
                        }
                )
        );
    }

    /**
     * Method called when user clicks "check" button to post image
     */
    private void publishPost() {
        MessageHelper.openLoadingDialog(this, "Salvando imagem, aguarde");
        final String imageId = FirebaseConfig.getFirebaseDatabase()
                .child(Constants.PostNode.KEY).push().getKey();

        StorageReference storageRef = FirebaseConfig.getFirebaseStorage()
                .child(Constants.Storage.IMAGES)
                .child(Constants.Storage.POST)
                .child(loggedUser.getId())
                .child(imageId + Constants.Storage.JPEG);

        byte[] imgData = BitmapHelper.bitmapToByteArray(filteredImage);

        UploadTask uploadTask = storageRef.putBytes(imgData);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                MessageHelper.closeLoadingDialog();
                MessageHelper.showLongToast("Erro ao fazer upload da imagem, tente novamente mais tarde");
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        final Post post = new Post();
                        post.setId(imageId);
                        post.setUserId(loggedUser.getId());
                        post.setImagePath(uri.toString());
                        if (imageDesc.getText() != null)
                            post.setDesc(imageDesc.getText().toString());

                        loggedUser.incrementCountPosts();
                        if (PostHelper.saveOnDatabase(post, loggedUser.getFollowersId())
                                && UserHelper.updateOnDatabase(loggedUser)) {



                            MessageHelper.showLongToast("Sucesso ao fazer upload da imagem");
                            finish();
                        }
                        else
                            MessageHelper.showLongToast("Erro ao fazer upload da imagem, tente novamente mais tarde");

                        MessageHelper.closeLoadingDialog();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_save_post:
                    publishPost();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}