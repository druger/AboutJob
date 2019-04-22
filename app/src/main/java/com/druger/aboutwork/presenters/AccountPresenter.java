package com.druger.aboutwork.presenters;

import android.net.Uri;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.druger.aboutwork.R;
import com.druger.aboutwork.db.FirebaseHelper;
import com.druger.aboutwork.db.RealmHelper;
import com.druger.aboutwork.interfaces.view.AccountView;
import com.druger.aboutwork.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import javax.inject.Inject;

/**
 * Created by druger on 09.05.2017.
 */

@InjectViewState
public class AccountPresenter extends BasePresenter<AccountView> {

    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    @SuppressWarnings("FieldCanBeLocal")
    private UploadTask uploadTask;
    private DatabaseReference dbReference;
    private ValueEventListener valueEventListener;

    @Inject
    public AccountPresenter(RealmHelper realmHelper) {
        storage = FirebaseStorage.getInstance();
        this.realmHelper = realmHelper;
    }

    public void getUserInfo() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

            dbReference = FirebaseDatabase.getInstance().getReference();
            downloadPhoto();
            getHeaderName();
            getViewState().showEmail(user.getEmail());
            getViewState().showName(user.getDisplayName());
        } else getViewState().showAuthAccess();
    }

    private void getHeaderName() {
        Query queryUser = FirebaseHelper.INSTANCE.getUser(dbReference, user.getUid());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        getViewState().showHeaderName(user.getName());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        queryUser.addValueEventListener(valueEventListener);
    }

    public void logout() {
        realmHelper.deleteAllData();
        getUserInfo();
    }

    public void savePhoto(Uri imgUri) {
        Uri file = Uri.fromFile(new File(imgUri.getPath()));
        storageRef = FirebaseHelper.INSTANCE.savePhoto(storage, user.getUid());
        uploadTask = storageRef.putFile(file);
        uploadTask.addOnSuccessListener(taskSnapshot ->
                getViewState().setupPhoto(imgUri)).addOnFailureListener(e -> {
            Log.d(TAG, e.getMessage());
            getViewState().showToast(R.string.upload_error);
        });
    }

    private void downloadPhoto() {
        storageRef = FirebaseHelper.INSTANCE.downloadPhoto(storage, user.getUid());
        getViewState().showPhoto(storageRef);
    }

    public void removeListeners() {
        if (dbReference != null && valueEventListener != null) {
            dbReference.removeEventListener(valueEventListener);
        }
    }

    public void removeAccount() {
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                            getViewState().showToast(R.string.profile_deleted);
                            getViewState().showMainActivity();
                        } else {
                            getViewState().showToast(R.string.failed_delete_user);
                        }
                    });
        }
    }
}
