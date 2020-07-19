package com.example.instagram_clone.model.user;

import android.net.Uri;
import android.util.Log;

import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.example.instagram_clone.utils.MessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import androidx.annotation.NonNull;

public class UserHelper {

    public static final String TAG = "UserHelper";

    public static User getLogged() {
        FirebaseUser firebaseUser = FirebaseConfig.getAuth().getCurrentUser();
        if (firebaseUser == null)
            return null;
        else {
            return new User(
                    firebaseUser.getUid(),
                    firebaseUser.getDisplayName(),
                    firebaseUser.getEmail(),
                    firebaseUser.getPhotoUrl()
            );
        }
    }

    /**
     * Sve user on database within his id
     * @param user
     * @return boolean to control success of operations
     */
    public static boolean updateOnDatabase(User user) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.UsersNode.KEY)
                    .child(user.getId())
                    .setValue(user);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "saveOnDatabase: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update user profile name on Firebase Auth
     * @return boolean to control success of operation
     */
    public static boolean updateProfileName(String name) {
        try {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            FirebaseConfig.getAuth().getCurrentUser().updateProfile(profile);
            return true;
        } catch (Exception e) {
            MessageHelper.showLongToast("Erro ao atualizar o nome de usuário: " + e.getMessage());
            Log.e(TAG, "updateUserProfileAuth: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update user profile image on Firebase Auth
     * @return boolean to control success of operation
     */
    public static boolean updateProfilePicture(Uri picture) {
        try {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(picture)
                    .build();
            FirebaseConfig.getAuth().getCurrentUser().updateProfile(profile);
            return true;
        } catch (Exception e) {
            MessageHelper.showLongToast("Erro ao atualizar a imagem de perfil do usuário" + e.getMessage());
            Log.e(TAG, "updateProfilePicture: " + e.getMessage());
            return false;
        }
    }

    private void teste(@NonNull Task<Void> task) throws Exception {
        throw  task.getException();
    }

    /**
     * Update user profile email on Firebase Auth
     * @return boolean to controll success of operation
     */
    public static void updateEmailProfile(final User user, final String newEmail) {
        final String prefixErrorMessage = "erro ao atualizar o email: ";
        // getAuth credentials from user for re-authentication
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), user.getPassword()); // current login credentials

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.reauthenticate(credential)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        // here is safe to update email[
                        firebaseUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.setEmail(newEmail);
                                    updateOnDatabase(user);
                                    MessageHelper.showLongToast("Sucesso ao atualizar o email");
                                } else {
                                    try {
                                        throw (task.getException());
                                    } catch (Exception e) {
                                        MessageHelper.showLongToast(prefixErrorMessage + e.getMessage());
                                        Log.d(TAG, "updateEmailProfile - updateEmail" + e.getMessage());
                                    }
                                }
                            }
                        });
                    } else {
                        try {
                            throw (task.getException());
                        } catch (Exception e) {
                            MessageHelper.showLongToast(prefixErrorMessage + e.getMessage());
                            Log.d(TAG, "updateEmailProfile: - reauthenticate: " + e.getMessage());
                        }
                    }
                }
            });
    }
}
