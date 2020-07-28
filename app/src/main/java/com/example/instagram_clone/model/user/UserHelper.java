package com.example.instagram_clone.model.user;

import android.net.Uri;
import android.util.Log;

import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.example.instagram_clone.utils.FollowHelper;
import com.example.instagram_clone.utils.MessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
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
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

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
     * Be sure to call this method with a complete user info that contains all his
     * info including following and followers users
     *
     * The method uses a async control with behavior subject that checks if all
     * deletions on database and storage are finished (posts, storage pictures, following info, etc)
     * before deleting user node completely.
     *
     * if you want to add one step more before delete user, follow this steps
     * 1) create a 'final int' X with a new unique value that represents this deletion
     * 2) if this deletion is needed, increment deleteCounterTarget with X before entering on async call
     * 3) call taskFinished(X) to increment deleteCounter
     * 4) when deleteCounter is equal to deleteCoutnerTarget, critique area are accessed and deletion is maded
     *
     * @param basicInfoUser
     */
    public static int deleteCounter = 0;
    public static int deleteCounterTarget = 0;
    public static final PublishSubject<Integer> taskFinished = PublishSubject.create();
    public static void deleteUserPermanently(final User basicInfoUser) {

        final int DELETE_POSTS_FINISHED = 1000;
        final int DELETE_FOLLOWERS_FINISHED = 2000;
        final int DELETE_FOLLOWING_FINISHED = 3000;
        final int DELETE_STORAGE_PROFILE_FINISHED = 4000;
        // obs: we are not deleting storage posts because firebase doesn't have folder deletion.
        // delete if manually
        try  {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(basicInfoUser.getEmail(), basicInfoUser.getPassword());
            final FirebaseUser firebaseUser = FirebaseConfig.getAuth().getCurrentUser();

            firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull final Task<Void> task) {
                    if (task.isSuccessful()) {
                        getLoggedCompleteInfo().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final User user = dataSnapshot.getValue(User.class);

                                // remove all posts
                                if (user.getCountPosts() != null && user.getCountPosts() != 0 ) {
                                    deleteCounterTarget = deleteCounterTarget + DELETE_POSTS_FINISHED;
                                    FirebaseConfig.getFirebaseDatabase()
                                            .child(Constants.PostNode.KEY)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(user.getId())) {
                                                        dataSnapshot.child(user.getId()).getRef().removeValue();
                                                    }
                                                    taskFinished.onNext(DELETE_POSTS_FINISHED);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) { taskFinished.onNext(DELETE_POSTS_FINISHED); }
                                            });
                                }

                                // unfollow all users
                                if (user.getFollowingsId() != null) {
                                    deleteCounterTarget = deleteCounterTarget + DELETE_FOLLOWING_FINISHED;
                                    for (String followingId : user.getFollowingsId()) {
                                        FirebaseConfig.getFirebaseDatabase()
                                                .child(Constants.UsersNode.KEY)
                                                .child(followingId)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        User followingUser = dataSnapshot.getValue(User.class);
                                                        FollowHelper.unfollow(user, followingUser);
                                                        taskFinished.onNext(DELETE_FOLLOWING_FINISHED);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) { taskFinished.onNext(DELETE_FOLLOWING_FINISHED); }
                                                });
                                    }

                                }

                                // be unfollowed from all users
                                if (user.getFollowersId() != null) {
                                    deleteCounterTarget = deleteCounterTarget + DELETE_FOLLOWERS_FINISHED;
                                    for (String followerId : user.getFollowersId()) {
                                        FirebaseConfig.getFirebaseDatabase()
                                                .child(Constants.UsersNode.KEY)
                                                .child(followerId)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        User followerUser = dataSnapshot.getValue(User.class);
                                                        FollowHelper.unfollow(followerUser, user);
                                                        taskFinished.onNext(DELETE_FOLLOWERS_FINISHED);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) { taskFinished.onNext(DELETE_FOLLOWERS_FINISHED); }
                                                });
                                    }
                                }

                                if (user.getImagePath() != null) {
                                    deleteCounterTarget = deleteCounterTarget + DELETE_STORAGE_PROFILE_FINISHED;
                                    FirebaseConfig.getFirebaseStorage()
                                            .child(Constants.Storage.IMAGES)
                                            .child(Constants.Storage.PROFILE)
                                            .child(user.getId() + Constants.Storage.JPEG)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    taskFinished.onNext(DELETE_STORAGE_PROFILE_FINISHED);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    taskFinished.onNext(DELETE_STORAGE_PROFILE_FINISHED);
                                                }
                                            });
                                }

                                // observer to wait all the rest be deleted first, and then, delete user node and firebaseUser
                                // every time a node is value is found, deleteTarget is incremented, and we only access
                                Observer<Integer> taskFinishedObserver = new Observer<Integer>() {
                                    @Override
                                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) { }

                                    @Override
                                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Integer deleteAction) {
                                        deleteCounter = deleteCounter + deleteAction;

                                        Log.d(TAG, "onNext -       deleteCounter: " + deleteCounter);
                                        Log.d(TAG, "onNext - deleteCounterTarget: " + deleteCounterTarget);
                                        if (deleteCounter == deleteCounterTarget) {
                                            Log.d(TAG, "onNext: " + "entrei");
                                            // Removes from basicInfoUser node make sure to call this
                                            // Delete after all other database deleting
                                            FirebaseConfig.getFirebaseDatabase()
                                                    .child(Constants.UsersNode.KEY)
                                                    .child(user.getId())
                                                    .removeValue();

                                            // delete firebaseUser
                                            taskFinished.onComplete();
                                            firebaseUser.delete();
                                        }
                                    }

                                    @Override
                                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) { }

                                    @Override
                                    public void onComplete() { }
                                };
                                taskFinished.subscribe(taskFinishedObserver);
                                taskFinished.onNext(0); // just to start operation if anywhere else did

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });

                    } else {
                        try {
                            throw (task.getException());
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            MessageHelper.showLongToast("Senha incorreta");
                        } catch (Exception e) {
                                taskFinished.onError(e);
                            MessageHelper.showLongToast("Erro ao excluir o usuário: " + e.getMessage());
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "removeUserPermanently: " + e.getMessage() );
        }
    }
}
