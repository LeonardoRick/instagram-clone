package com.example.instagram_clone.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.instagram_clone.R;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.model.user.UserHelper;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.example.instagram_clone.utils.BitmapHelper;
import com.example.instagram_clone.utils.MessageHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextProfileName, editTextProfileEmail;

    private CircleImageView profileImage;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar Pefil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black); // chage toolbar back button icon

        initViewElements();
    }

    private void initViewElements() {
        profileImage = findViewById(R.id.circleImageViewEditProfile);
        progressBar = findViewById(R.id.progressBarChangeProfilePicture);

        editTextProfileName = findViewById(R.id.editTextProfileName);
        editTextProfileEmail = findViewById(R.id.editTextProfileEmail);
        recoverUserInfo();
    }

    private void recoverUserInfo() {
        User user = UserHelper.getLogged();
        if (user != null) {
            editTextProfileName.setText(UserHelper.getLogged().getName());
            editTextProfileEmail.setText(UserHelper.getLogged().getEmail());

            if (user.getImagePath() != null) {
                Uri uri = Uri.parse(user.getImagePath());
                Picasso.get().load(uri)
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .into(profileImage);
            }
        }
    }

    /**
     * update user info and save it on database and Firebase auth
     */
    public void saveUserProfileInfo(View view) {
        if (validateFields()) {
            User user = UserHelper.getLogged();
            String name = editTextProfileName.getText().toString();
            String email = editTextProfileEmail.getText().toString();

            if (!name.equals(user.getName())) {
                user.setName(name);
                UserHelper.updateProfileName(user.getName());
                if (UserHelper.updateOnDatabase(user)) {
                    MessageHelper.showLongToast("Nome atualizado com sucesso");
                };
            }

            if (!email.equals(user.getEmail()))
                updateEmail(user, email);
        }
    }

    /**
     * validate if both Name and email has a value before updating
     * @return boolean if all fields are filled
     */
    private boolean validateFields() {
        Editable profileName = editTextProfileName.getText();
        Editable profileEmail = editTextProfileEmail.getText();
        if (profileName != null && profileEmail != null) {
            if (!profileName.toString().isEmpty() && !profileEmail.toString().isEmpty())
                return true;
            return false;
        }
        return false;
    }

    /**
     * shows a dialog to user input his password since update email is a sensitive action
     * @return user password
     */
    private void updateEmail(final User user, final String newEmail) {
        final EditText input = MessageHelper.getInputToDialog();
        // Frame Layout
        FrameLayout container = new FrameLayout(this);
        container.addView(input);

        // AlertDialog
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(container);
        alert.setTitle("Informe sua senha para realizar a alteração do email");

        alert.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText() != null) {
                    user.setPassword(input.getText().toString());

                    UserHelper.updateEmailProfile(user, newEmail);
                } else
                    MessageHelper.showLongToast("Senha não preenchida");

            }
        });

        alert.setNegativeButton("Cancelar", null);
        alert.create();
        alert.show();
    };


    /**
     * Change behavior of app backbutton to work like native smartphone backbutton
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    /* **************** START - Process of updating profile image  **************** */
    public void validatePermission(View view) {
        ActivityCompat.requestPermissions(
                this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                Constants.FeatureRequest.STORAGE
        );
    }

    private void saveImage(final Bitmap image) {
        byte[] imgData = BitmapHelper.bitmapToByteArray(image);

        StorageReference imageStorageRef = FirebaseConfig.getFirebaseStorage()
                .child(Constants.Storage.IMAGES)
                .child(Constants.Storage.PROFILE)
                .child(UserHelper.getLogged().getId() + Constants.Storage.JPEG); // Save image with user id name

        UploadTask uploadTask = imageStorageRef.putBytes(imgData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                MessageHelper.showLongToast("Erro ao fazer upload da imagem" + e.getMessage());
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        User user = UserHelper.getLogged();
                        user.setImagePath(uri.toString());

                        if (UserHelper.updateProfilePicture(uri) &&
                            UserHelper.updateOnDatabase(user)) {

                            profileImage.setImageBitmap(image);
                            MessageHelper.showLongToast("Imagem atualizada com sucesso");
                        } else
                            MessageHelper.showLongToast("Erro ao atualizar a imagem, tente novamente mais tarde");

                        progressBar.setVisibility(View.GONE);
                    }
                });

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) // if user accepted
            startIntentUploadPicture(requestCode);
        else if (shouldShowRequestPermissionRationale(permissions[0])) // if user denied first time
            alertUserPermissionNeeded(requestCode);
        else // if user denied more than one time (returns false to last method, so drops on this case)
            showDefaultSettingsPermissionRequired();
    }

    /**
     * Method called when user grant permission and we want to access gallery
     * @param requestCode
     */
    private void startIntentUploadPicture(int requestCode) {
        Intent intent = null;
        switch (requestCode) {
            case Constants.FeatureRequest.STORAGE:
                progressBar.setVisibility(View.VISIBLE);
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            break;
        }
        try  {
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    /**
     * User denied permission for the first time
     */
    private void alertUserPermissionNeeded(int requestCode) {
        String msg = "";
        if (requestCode == Constants.FeatureRequest.STORAGE)
            msg = "Vocẽ irá precisar conceder permissão à galeria se quiser escolher uma foto de galeria";
        else
            msg = "Você precisa conceder permissão para utilizar esse recurso do celular";

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Permissão Necessária");
        dialog.setMessage(msg);
        dialog.setPositiveButton("Entendi", null);

        dialog.create();
        dialog.show();
    }

    /**
     * User denied permission for second time
     */
    private void showDefaultSettingsPermissionRequired() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Mudar a permissão em configurações");
        dialog.setMessage("Clique em Configurações para manualmente permitir o aplicativo acessar a galeria");
        dialog.setPositiveButton("Configurações", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openPhoneSettings();
            }
        });
        dialog.create();
        dialog.show();
    }

    /**
     * Open native smartphone Settings
     */
    private void openPhoneSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        // current activity will be notified when config is finished (back button is pressed)
        startActivityForResult(intent, Constants.FeatureRequest.SETTINGS);
    }

    /**
     * Method that returns to activity from any ActivityForResult() call with some info selected
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap image = null;
            try {
                switch (requestCode) {
                    case Constants.FeatureRequest.STORAGE:
                        Uri selectedImageUri = data.getData();
                        image = BitmapHelper.getBitmap(this, selectedImageUri);
                    break;
                }

                if (image != null) saveImage(image);
            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        } else progressBar.setVisibility(View.GONE);
    }
    /* **************** END - Process of updating profile image  **************** */

    public void deleteUserForever(View view) {
        final EditText input = MessageHelper.getInputToDialog();

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.addView(input);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(frameLayout);
        alert.setTitle("Você tem certeza que deseja fazer isso?");
        alert.setMessage("Essa operação não pode ser desfeita e você perderá todos os dados salvos até hoje." +
                " Se quiser prosseguir, digite sua senha");
        alert.setNegativeButton("Cancelar", null);
        alert.setPositiveButton("Sim, tenho certeza", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText() != null) {
                    User user = UserHelper.getLogged();
                    user.setPassword(input.getText().toString());
                    UserHelper.deleteUserPermanently(user);
                }
            }
        });
        alert.create();
        alert.show();
    }
}