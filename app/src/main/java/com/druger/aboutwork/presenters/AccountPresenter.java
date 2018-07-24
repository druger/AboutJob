package com.druger.aboutwork.presenters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

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
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;

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

    private Context context;
    private Uri selectedImgUri;

    @Inject
    public AccountPresenter(RealmHelper realmHelper) {
        storage = FirebaseStorage.getInstance();
        this.realmHelper = realmHelper;
    }

    public void getUserInfo() {
        dbReference = FirebaseDatabase.getInstance().getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            getViewState().showEmail(user.getEmail());
            downloadPhoto();
            getName();
        }
    }

    private void getName() {
        Query queryUser = FirebaseHelper.getUser(dbReference, user.getUid());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        getViewState().showName(user.getName());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        queryUser.addValueEventListener(valueEventListener);
    }

    public void clickChangeName() {
        getViewState().changeName(user.getUid());
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        realmHelper.deleteAllData();
    }

    public void clickOpenSettings() {
        getViewState().openSettings();
    }

    public void clickOpenMyReviews() {
        getViewState().openMyReviews(user.getUid());
    }

    public void changeUserName(String userName, String userId) {
        FirebaseHelper.changeUserName(userName, userId);
    }

    public void checkActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        this.context = context;
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            pickImage(data);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            cropImage(resultCode, data);
        }
    }

    private void cropImage(int resultCode, Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            selectedImgUri = result.getUri();
            savePhoto();
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Log.e(TAG, "Cropping failed: " + result.getError().getMessage());
        }
    }

    private void savePhoto() {
        Uri file = Uri.fromFile(new File(selectedImgUri.getPath()));
        storageRef = FirebaseHelper.savePhoto(storage, user.getUid());
        uploadTask = storageRef.putFile(file);
        uploadTask.addOnSuccessListener(taskSnapshot ->
                getViewState().setupPhoto(selectedImgUri)).addOnFailureListener(e -> {
            Log.d(TAG, e.getMessage());
            Toast.makeText(context, R.string.upload_error, Toast.LENGTH_SHORT).show();
        });
    }

    private void pickImage(Intent data) {
        Uri imgUri = CropImage.getPickImageResultUri(context, data);
        if (CropImage.isReadExternalStoragePermissionsRequired(context, imgUri)) {
            selectedImgUri = imgUri;
            getViewState().checkPermissionReadExternal();
        } else {
            Log.d(TAG, "pickImage: startCropImageActivity");
            getViewState().startCropImageActivity(imgUri);
        }
    }

    private void downloadPhoto() {
        storageRef = FirebaseHelper.downloadPhoto(storage, user.getUid());
        getViewState().showPhoto(storageRef);
    }

    public void removeListeners() {
        if (valueEventListener != null) {
            dbReference.removeEventListener(valueEventListener);
        }
    }
}
