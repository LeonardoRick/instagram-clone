package com.example.instagram_clone.model.user;

import android.net.Uri;
import android.util.Log;

import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.example.instagram_clone.utils.FollowHelper;
import com.example.instagram_clone.utils.MessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.security.AuthProvider;
import java.util.HashMap;
import java.util.Map;

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
     * User when you need to recover more info from user
     * than firebase auth provides (name, email, image);
     *
     * If you want to recover number of followers, for example
     * you need to recover this complete info from user
     *
     * add a value event listener to recover info
     * @return user;
     */
    public static DatabaseReference getLoggedCompleteInfo() {
       try {
           return FirebaseConfig.getFirebaseDatabase()
                   .child(Constants.UsersNode.KEY)
                   .child(getLogged().getId());
       } catch (Exception e) {
           return null;
       }
    };

    /**
     * Save user on database within his id.
     * Used only creating user o RegisterActivity
     * @param user
     * @return boolean to control success of operations
     */
    public static boolean saveOnDatabase(User user) {
        try {
            user.setCountPosts(0);
            user.setCountFollowers(0);
            user.setCountFollowing(0);

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
     *
     * @param user to update each old value on database
     * @return boolean that show if operation was sucessfull
     */
    public static boolean updateOnDatabase(User user) {
        try {
            Map<String, Object> userMap = convertUserToMap(user);
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.UsersNode.KEY)
                    .child(user.getId())
                    .updateChildren(userMap);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "updateOnDatabase: " + e.getMessage() );
            return false;
        }
    }

    /**
     * @param user to be converted to hashMap to firebase .updateChildren() accepts
     * @return Map<String, Object> where Object is user info
     */
    public static Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> userMap = new HashMap<>();

        if (user.getId() != null) userMap.put(Constants.UsersNode.ID, user.getId());
        if (user.getName() != null) {
            userMap.put(Constants.UsersNode.NAME, user.getName());
            userMap.put(Constants.UsersNode.NAME_TO_SEARCH, user.getName().toLowerCase());
        };
        if (user.getEmail() != null) userMap.put(Constants.UsersNode.EMAIL, user.getEmail());
        if (user.getImagePath() != null) userMap.put(Constants.UsersNode.IMAGE_PATH, user.getImagePath());
        
        if (user.getCountPosts() != null) userMap.put(Constants.UsersNode.COUNT_POSTS, user.getCountPosts());
        if (user.getCountFollowers() != null)userMap.put(Constants.UsersNode.COUNT_FOLLOWERS, user.getCountFollowers());
        if (user.getCountFollowing() != null) userMap.put(Constants.UsersNode.COUNT_FOLLOWING, user.getCountFollowing());
        
        if (user.getFollowersId() != null) userMap.put(Constants.UsersNode.FOLLOWERS_ID, user.getFollowersId());
        if (user.getFollowingsId() != null) userMap.put(Constants.UsersNode.FOLLOWINGS_ID, user.getFollowingsId());
        return userMap;
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


    /**
     * Deletes basicInfoUser permanently from auth storage and  everything related to the app
     * Since it's a sensitive  operation, basicInfoUser need's to reauthenticate.
     * Be sure to update this method everytime you add new nodes on database that uses basicInfoUser info
     *
     * Be sure to call this method with a complete basicInfoUser info that contains all his
     * info including following and followers users
     * @param basicInfoUser
     */
    public static void deleteUserPermanently(final User basicInfoUser) {
        try  {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(basicInfoUser.getEmail(), basicInfoUser.getPassword());
            final FirebaseUser firebaseUser = FirebaseConfig.getAuth().getCurrentUser();

            firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // remove all posts
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                FirebaseConfig.getFirebaseDatabase()
                                        .child(Constants.PostNode.KEY)
                                        .child(basicInfoUser.getId())
                                        .removeValue();
                            }
                        }).start();

                        getLoggedCompleteInfo().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                final User user = dataSnapshot.getValue(User.class);
                                // new thread to unfollow all users
                                if (user.getFollowingsId() != null) {
                                    for (String followingId : user.getFollowingsId()) {
                                        FirebaseConfig.getFirebaseDatabase()
                                                .child(Constants.UsersNode.KEY)
                                                .child(followingId)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        User followingUser = dataSnapshot.getValue(User.class);
                                                        FollowHelper.unfollow(user, followingUser);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                                });
                                    }

                                }

                                // be unfollowed from all users
                                if (user.getFollowersId() != null) {
                                    for (String followersId : user.getFollowersId()) {
                                        FirebaseConfig.getFirebaseDatabase()
                                                .child(Constants.UsersNode.KEY)
                                                .child(followersId)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        User followerUser = dataSnapshot.getValue(User.class);
                                                        FollowHelper.unfollow(followerUser, user);

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                                });
                                    }
                                }

                                // removes from basicInfoUser node make sure to call this
                                // delete after all other database deleting
                                FirebaseConfig.getFirebaseDatabase()
                                        .child(Constants.UsersNode.KEY)
                                        .child(user.getId())
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                } else {
                                                    try {
                                                        throw task.getException();
                                                    } catch (Exception e) {
                                                        Log.d(TAG, "onComplete: " + e.getMessage());
                                                    }
                                                }
                                            }
                                        });

                                // call this at the end
                                firebaseUser.delete();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
//                        TODO
//                        REMOVER STORAGE
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "removeUserPermanently: " + e.getMessage() );
        }
    }
}
