package com.example.instagram_clone.fragment;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.instagram_clone.R;
import com.example.instagram_clone.activity.FilterActivity;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.BitmapHelper;

public class PostFragment extends Fragment {

    private Button openGallery, openCamera;
    public PostFragment() {
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
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        initViewElements(view);
        return view;
    }

    private void initViewElements(View view) {
        openGallery = view.findViewById(R.id.openGallery);
        openCamera = view.findViewById(R.id.openCamera);

        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.FeatureRequest.STORAGE
                );
            }
        });

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[] {Manifest.permission.CAMERA},
                        Constants.FeatureRequest.CAMERA
                );
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) // if user granted
            startIntentUploadPicture(requestCode);
        else if (shouldShowRequestPermissionRationale(permissions[0])) // if user denied first time
            alertUserPermissionNeeded(requestCode);
        else // if user denied more than one time (returns false to above method check, so drops on this case
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
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                break;
            case Constants.FeatureRequest.CAMERA:
                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                break;
        }
        try {
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {

                startActivityForResult(intent, requestCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * User denied permission for the first time
     * @param requestCode
     */
    private void alertUserPermissionNeeded(int requestCode) {
        String msg = "";
        switch (requestCode) {
            case Constants.FeatureRequest.STORAGE:
                msg = "Você irá precisar conceder permissão à galeria se quiser fazer upload de uma foto";
                break;
            case Constants.FeatureRequest.CAMERA:
                msg = "Você irá precisar conceder permissão à câmera se quiser fazer upload de uma foto";
                break;
            default:
                msg = "Coxê irá precisar conceder permissão para acessar o recurso";
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Mudar a permissão em configurações");
        dialog.setMessage("Clique em Configurações para manualmente conceder as permissões necessárias");
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
     * Open native smart phone settings
     */
    private void openPhoneSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, Constants.FeatureRequest.SETTINGS);
    }

    /**
     * Method that returns to activity from any ActivityForResult() call with some info selected
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            Uri imageUri = null;
            try {
                switch (requestCode) {
                    case Constants.FeatureRequest.STORAGE:
                        imageUri = data.getData();
                        break;
                    case Constants.FeatureRequest.CAMERA:
                        Bitmap image = (Bitmap) data.getExtras().get("data");
                        imageUri = BitmapHelper.getBitmapUri(getContext(), image);
                        break;
                }

                if (imageUri != null)
                    startFilterActivity(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startFilterActivity(Uri imageUri) {
        Intent intent = new Intent(getActivity(), FilterActivity.class);
        intent.putExtra(Constants.IntentKey.SELECTED_PICTURE, imageUri.toString());
        startActivity(intent);
    }
}